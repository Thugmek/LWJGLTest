import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;


public class Main {

    public Main(){

        glfwInit();

        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwWindowHint(GLFW_RED_BITS, mode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());


        long win = glfwCreateWindow(mode.width(),mode.height(),"LWJGL project",glfwGetPrimaryMonitor(),0);

        glfwShowWindow(win);
        glfwMakeContextCurrent(win);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ShaderProgram shaderProgram = null;
        try {
            shaderProgram = new ShaderProgram();
            shaderProgram.createVertexShader(loadString("shaders/shader.vs"));
            shaderProgram.createFragmentShader(loadString("shaders/shader.fs"));
            shaderProgram.createGeometryShader(loadString("shaders/shader.gs"));
            shaderProgram.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ShaderProgram wireShaderProgram = null;
        try {
            wireShaderProgram = new ShaderProgram();
            wireShaderProgram.createVertexShader(loadString("shaders/shader.vs"));
            wireShaderProgram.createFragmentShader(loadString("shaders/wireShader.fs"));
            wireShaderProgram.createGeometryShader(loadString("shaders/wireShader.gs"));
            wireShaderProgram.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ShaderProgram particleShaderProgram = null;
        try {
            particleShaderProgram = new ShaderProgram();
            particleShaderProgram.createVertexShader(loadString("shaders/shader.vs"));
            particleShaderProgram.createFragmentShader(loadString("shaders/particleShader.fs"));
            particleShaderProgram.createGeometryShader(loadString("shaders/particleShader.gs"));
            particleShaderProgram.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Camera camera = new Camera(new Vector3f(0,30,0),4,-0.7f);

        Terrain t = new Terrain(shaderProgram, new Vector3f(0,0,0));
        Cube c = new Cube(wireShaderProgram);

        Particle p = new Particle();
        p.setShader(particleShaderProgram);
        p.setPos(new Vector3f(0,0,0));

        t.setCursor(c);

        KeyboardInput.setWindow(win);
        MouseInput.init(win);

        long time = getTime()-1;

        glClearColor(0.0f,0f,0.5f,1);

        while(!glfwWindowShouldClose(win)){
            long newTime = getTime()+1000000/60;
            float delta = (newTime - time)/1000000.0f;
            time = newTime;

            //System.out.println("FPS: " + 1/delta);

            glfwPollEvents();

            if(KeyboardInput.getKey(GLFW_KEY_ESCAPE) == 1){
                glfwSetWindowShouldClose(win,true);
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //camera.setPos(new Vector3f((float)(Math.cos(time/1000000.0f)*1),(float)(Math.sin(time/1000000.0f)*1),20));
            camera.onUpdate(delta*20);

            t.update(delta*5);

            camera.forShader(shaderProgram);
            camera.forShader(wireShaderProgram);
            camera.forShader(particleShaderProgram);

            t.render();

            FloatBuffer buff = BufferUtils.createFloatBuffer(1);

            glReadPixels(mode.width()/2,mode.height()/2,1,1,GL_DEPTH_COMPONENT,GL_FLOAT,buff);

            float f = buff.get(0);
            Vector3f cur = camera.getCursor(f);
            if(cur != null) {
                c.setPos(new Vector3f(Math.round(cur.x),Math.round(cur.y),Math.round(cur.z)));
                c.render();
            }
            p.render();

            glfwSwapBuffers(win);

            //FPS limit
            while(time > getTime());

        }
        //shaderProgram.cleanup();
        glfwTerminate();
    }

    public static void main(String[] args) {
        new Main();
    }

    private static String loadString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/" + filePath));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private long getTime() {
        return (long)(glfwGetTime() * 1000000);
    }

}
