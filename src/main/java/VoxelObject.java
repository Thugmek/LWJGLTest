import org.joml.Random;
import org.joml.Vector3f;

public class VoxelObject {
    public int v;
    public Vector3f color;

    public VoxelObject(Random r){
        v = r.nextInt(2);
        color = new Vector3f(r.nextFloat(),r.nextFloat(),r.nextFloat()).normalize();
    }

    public VoxelObject(int i, Vector3f c){
        v = i;
        color = c;
    }
}
