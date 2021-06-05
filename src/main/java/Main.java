import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


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

        ShaderProgram shaderProgram = null;
        try {
            shaderProgram = new ShaderProgram();
            shaderProgram.createVertexShader(loadString("shader.vs"));
            shaderProgram.createFragmentShader(loadString("shader.fs"));
            shaderProgram.createGeometryShader(loadString("shader.gs"));
            shaderProgram.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ShaderProgram wireShaderProgram = null;
        try {
            wireShaderProgram = new ShaderProgram();
            wireShaderProgram.createVertexShader(loadString("shader.vs"));
            wireShaderProgram.createFragmentShader(loadString("wireShader.fs"));
            wireShaderProgram.createGeometryShader(loadString("wireShader.gs"));
            wireShaderProgram.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Camera camera = new Camera(new Vector3f(0,30,0),4,-0.7f);

        Terrain t = new Terrain(shaderProgram, new Vector3f(0,0,0));
        Cube c = new Cube(wireShaderProgram);

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

            t.render();
            c.render();

            //mod.getTransform().rotate(0.000001f*delta,new Vector3f(0,1,0.2f));

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
