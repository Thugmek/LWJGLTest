import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardInput {

    private static long window = 0;

    public static void setWindow(long window){
        KeyboardInput.window = window;
    }

    public static int getKey(int key){
        return glfwGetKey(window,key);
    }
}