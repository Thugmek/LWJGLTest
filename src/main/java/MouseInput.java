
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private static long window = 0;

    private static Vector2f lastPos;

    public static void init(long window){
        MouseInput.window = window;
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (glfwRawMouseMotionSupported())
            glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);

        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);

        lastPos = new Vector2f((float)x.get(),(float)y.get());

    }

    /*public static void get(){
        glfwGetCursorPos(window, x, y);
        int mouseX = (int) Math.round(x.get());
        int mouseY = (int) Math.round(y.get());

        System.out.println("X: " + mouseX + " Y: " + mouseY);
    }*/

    public static Vector2f getDeltaPos(){

        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);

        Vector2f pos = new Vector2f((float)x.get(),(float)y.get());

        Vector2f temp = new Vector2f(pos);

        pos.sub(lastPos);
        lastPos = temp;

        return pos;
    }

    public static int getMouseButton(int button){
        return glfwGetMouseButton(window,button);
    }
}
