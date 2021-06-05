/*import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;*/

import static org.lwjgl.opengl.GL32.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Model {

    private int tris;
    private int vb_id;
    private int cb_id;
    private ShaderProgram shader;
    private Vector3f color;

    private Vector3f pos;

    public Model(float[] verts, float[] cols){
        pos = new Vector3f(0,0,0);
        color = new Vector3f(1,0,1);

        tris = verts.length/3;

        FloatBuffer buff = BufferUtils.createFloatBuffer(verts.length);
        buff.put(verts);
        buff.flip();

        vb_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vb_id);
        glBufferData(GL_ARRAY_BUFFER,buff,GL_STATIC_DRAW);

        buff = BufferUtils.createFloatBuffer(cols.length);
        buff.put(cols);
        buff.flip();

        cb_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,cb_id);
        glBufferData(GL_ARRAY_BUFFER,buff,GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER,0);

        System.out.println("New model with " + tris + " verts. Buffer id: " + vb_id);
    }

    public void setShader(ShaderProgram shader){
        this.shader = shader;
    }

    public void render(){

        //calculate transform
        Matrix4f transform = new Matrix4f();
        transform.translate(pos);

        //set shader
        shader.bind();
        shader.setObjectTransform(transform);
        shader.setObjectColor(color);
        //render
        glEnable(GL_VERTEX_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER,vb_id);
        glVertexPointer(3,GL_FLOAT,0,0);

        glBindBuffer(GL_ARRAY_BUFFER,cb_id);
        //glColorPointer(3,GL_FLOAT,0,0);
        glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
        glDrawArrays(GL_TRIANGLES,0,tris);

        glBindBuffer(GL_ARRAY_BUFFER,0);

        glDisable(GL_VERTEX_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        shader.unbind();
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public void setColor(Vector3f color){
        this.color = color;
    }
}
