import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;



public class Camera {
    private float azimuth = 0;
    private float zenith = 0;
    private Vector3f pos;
    private float fov = 1;

    private Vector3f front;
    private Vector3f up;
    private Vector3f left;
    private Matrix4f worldMat;

    public Camera(Vector3f pos, float azimuth, float zenith){
        this.pos = pos;
        this.azimuth = azimuth;
        this.zenith = zenith;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getZenith() {
        return zenith;
    }

    public void setZenith(float zenith) {
        this.zenith = zenith;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public void recalculate(){

        //transformation matrix
        worldMat = new Matrix4f();
        Vector3f v = new Vector3f(pos);

        worldMat.rotate(zenith,new Vector3f(-1,0,0));
        worldMat.rotate(azimuth,new Vector3f(0,-1,0));
        worldMat.translate(v.mul(-1));

        //up-vector
        up = new Vector3f(0,1,0);

        //front-vector
        //front = new Vector3f(-(float)Math.sin(azimuth),0,-(float)Math.cos(azimuth));
        front = new Vector3f(-(float)Math.sin(azimuth)*(float)Math.cos(zenith),(float)Math.sin(zenith),-(float)Math.cos(azimuth)*(float)Math.cos(zenith));

        left = new Vector3f(-(float)Math.cos(azimuth),0,(float)Math.sin(azimuth));

    }

    public void onUpdate(float delta){
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_W) == 1){
            pos.add(new Vector3f(front).mul(delta));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_S) == 1){
            pos.add(new Vector3f(front).mul(-delta));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_A) == 1){
            pos.add(new Vector3f(left).mul(delta));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_D) == 1){
            pos.add(new Vector3f(left).mul(-delta));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_Q) == 1){
            pos.add(new Vector3f(0,-delta,0));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_E) == 1){
            pos.add(new Vector3f(0,delta,0));
        }

        Vector2f v = MouseInput.getDeltaPos();
        azimuth -= v.x*0.005f;
        zenith -= v.y*0.005f;

        if(zenith < -Math.PI/2) zenith = -(float)Math.PI/2;
        if(zenith > Math.PI/2) zenith = (float)Math.PI/2;

        recalculate();
    }

    public void forShader(ShaderProgram shader){
        shader.bind();

        shader.setWorldTransform(worldMat);
        shader.setProjectionTransform(new Matrix4f().setPerspective(fov,8.0f/6.0f,0.01f,1000));
        shader.unbind();
    }
}
