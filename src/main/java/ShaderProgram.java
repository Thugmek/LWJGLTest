import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL32.*;

public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;
    private int fragmentShaderId;
    private int geometryShaderId;

    private int objectTransform;
    private int worldTransform;
    private int projectionTransform;
    private int objectColor;

    public ShaderProgram() throws Exception {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        System.out.println("Create vertex shader");
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        System.out.println("Create fragment shader");
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    public void createGeometryShader(String shaderCode) throws Exception {
        System.out.println("Create geometry shader");
        geometryShaderId = createShader(shaderCode, GL_GEOMETRY_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }
        if (geometryShaderId != 0) {
            glDetachShader(programId, geometryShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        objectTransform = glGetUniformLocation(programId,"objectTransform");
        worldTransform = glGetUniformLocation(programId,"worldTransform");
        projectionTransform = glGetUniformLocation(programId,"projectionTransform");
        objectColor = glGetUniformLocation(programId,"objectColor");

    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public void setUniform(String name, float value){
        int location = glGetUniformLocation(programId,name);
        if(location != -1){
            glUniform1f(location,value);
        }else{
            System.err.println("Can't get uniform location");
        }
    }

    public void setProjectionTransform(Matrix4f transform){
        float[] matrix = new float[16];
        transform.get(matrix);
        glUniformMatrix4fv(projectionTransform,false,matrix);
    }
    public void setWorldTransform(Matrix4f transform){
        float[] matrix = new float[16];
        transform.get(matrix);
        glUniformMatrix4fv(worldTransform,false,matrix);
    }
    public void setObjectTransform(Matrix4f transform){
        float[] matrix = new float[16];
        transform.get(matrix);
        glUniformMatrix4fv(objectTransform,false,matrix);
    }
    public void setObjectColor(Vector3f color){
        glUniform3f(objectColor,color.x,color.y,color.z);
    }

}
