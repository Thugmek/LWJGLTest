import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    public Main(){
        glfwInit();
        long win = glfwCreateWindow(800,600,"LWJGL project",0,0);

        glfwShowWindow(win);
        glfwMakeContextCurrent(win);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);

        ShaderProgram shaderProgram = null;
        try {
            shaderProgram = new ShaderProgram();
            shaderProgram.createVertexShader(loadString("C:\\Users\\Thugmek\\Documents\\LWJGLTest\\src\\main\\resources\\shader.vs"));
            shaderProgram.createFragmentShader(loadString("C:\\Users\\Thugmek\\Documents\\LWJGLTest\\src\\main\\resources\\shader.fs"));
            shaderProgram.createGeometryShader(loadString("C:\\Users\\Thugmek\\Documents\\LWJGLTest\\src\\main\\resources\\shader.gs"));
            shaderProgram.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Camera camera = new Camera(new Vector3f(0,30,0),4,-0.7f);

        Terrain t = new Terrain(shaderProgram);

        KeyboardInput.setWindow(win);

        long time = getTime()-1;

        while(!glfwWindowShouldClose(win)){
            long newTime = getTime()+1000000/60;
            float delta = (newTime - time)/1000000.0f;
            time = newTime;

            //System.out.println("FPS: " + 1/delta);

            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //camera.setPos(new Vector3f((float)(Math.cos(time/1000000.0f)*1),(float)(Math.sin(time/1000000.0f)*1),20));
            camera.onUpdate(delta*20);
            camera.onRender(shaderProgram);

            t.render();

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
            BufferedReader br = new BufferedReader(new FileReader(filePath));
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
