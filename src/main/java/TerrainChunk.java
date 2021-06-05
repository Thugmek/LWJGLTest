import org.joml.Vector3f;

import java.util.ArrayList;

public class TerrainChunk {

    private Model model;
    private IntObject[][][] map;
    int width;
    int height;
    int depth;

    private ShaderProgram sh;
    private Vector3f pos;

    public TerrainChunk(IntObject[][][] map, Vector3f pos, ShaderProgram sh){
        this.map = map;
        this.sh = sh;
        this.pos = pos;
        width = map.length;
        height = map[0].length;
        depth = map[0][0].length;

        recalculate();
    }

    public void render(){
        model.render();
    }

    public void recalculate(){
        ArrayList<float[]> voxels = new ArrayList<float[]>();
        int length = 0;

        for(int i = 0; i<width-1;i++){
            for(int j = 0; j<height-1;j++){
                for(int k = 0; k<depth-1;k++) {

                    int verts[][][] = new int[2][2][2];

                    verts[0][0][0] = map[i][j][k].v;
                    verts[1][0][0] = map[i+1][j][k].v;
                    verts[0][1][0] = map[i][j+1][k].v;
                    verts[1][1][0] = map[i+1][j+1][k].v;

                    verts[0][0][1] = map[i][j][k+1].v;
                    verts[1][0][1] = map[i+1][j][k+1].v;
                    verts[0][1][1] = map[i][j+1][k+1].v;
                    verts[1][1][1] = map[i+1][j+1][k+1].v;

                    float[] f = MarchCube.getVoxelFloats(verts);

                    for(int x = 0; x<f.length; x+=3){
                        f[x] += i;
                        f[x+1] += j;
                        f[x+2] += k;
                    }

                    length += f.length;
                    voxels.add(f);
                }
            }
        }


        float objectData[] = new float[length];
        float objectColors[] = new float[length];
        int pointer = 0;
        int voxelsSize = voxels.size();


        for(int x = 0;x<voxelsSize;x++){
            int voxelLength = voxels.get(x).length;
            for(int y = 0;y<voxelLength;y+=3){
                objectData[pointer] = voxels.get(x)[y];
                objectData[pointer+1] = voxels.get(x)[y+1];
                objectData[pointer+2] = voxels.get(x)[y+2];

                objectColors[pointer] = 1;
                objectColors[pointer+1] = voxels.get(x)[y+1]/(height);
                objectColors[pointer+2] = 0;

                pointer+=3;
            }
        }

        model = new Model(objectData, objectColors);

        //model = new Cube(sh);
        model.setShader(sh);
        model.setColor(new Vector3f(1,0,0));
        model.setPos(pos);
    }

}
