import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Particle extends Model {

    public Particle(){

        tris = 1000;
        Random r = new Random(1223);

        float[] verts = new float[tris*3];

        for(int i = 0;i<tris*3;i++){
            verts[i] = r.nextFloat()*100;
        }

        FloatBuffer buff = BufferUtils.createFloatBuffer(verts.length);
        buff.put(verts);
        buff.flip();

        vb_id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vb_id);
        glBufferData(GL_ARRAY_BUFFER,buff,GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    public void render(){

        //calculate transform
        Matrix4f transform = new Matrix4f();
        transform.translate(pos);

        //set shader
        shader.bind();
        shader.setObjectTransform(transform);
        //render
        glEnable(GL_VERTEX_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER,vb_id);
        glVertexPointer(3,GL_FLOAT,0,0);

        glVertexAttribPointer(1,3,GL_FLOAT,false,0,0);
        glDrawArrays(GL_POINTS,0,tris);

        glBindBuffer(GL_ARRAY_BUFFER,0);

        glDisable(GL_VERTEX_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        shader.unbind();
    }
}
