import org.joml.Random;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Terrain {

    private Model model;
    private IntObject[][][] map;
    int width = 100;
    int height = 100;
    int depth = 100;

    Random r;

    public Terrain(ShaderProgram sh, Vector3f translate){
        System.out.println("generating terrain...");
        map = new IntObject[width][height][depth];

        r = new Random(314159);

        System.out.println("generating map...");

        generateMap();

        IntObject[][][] arr = new IntObject[10][10][10];

        for(int i = 0; i<10;i++){
            for(int j = 0; j<10;j++){
                for(int k = 0; k<10;k++) {
                    arr[i][j][k] = map[i][j][k];
                    //map[i][j][k] = 0;
                }
            }
        }

        new TerrainChunk(arr, sh);

        System.out.println("marching cubes...");

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

        System.out.println("merge voxels...");

        float objectData[] = new float[length];
        float objectColors[] = new float[length];
        int pointer = 0;
        int voxelsSize = voxels.size();

        int progress = 0;

        for(int x = 0;x<voxelsSize;x++){
            if((pointer * 100 / length) > progress+5) {
                progress = (pointer * 100 / length);
                System.out.println("progres: " + progress + "%");
            }
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

        System.out.println("terrain done...");

        model = new Model(objectData, objectColors);
        model.setShader(sh);
        model.setColor(new Vector3f(1,0,0));
        model.setPos(translate);

    }

    public void render(){
        model.render();
    }

    private void generateMap(){
        for(int i = 0; i<width;i++){
            for(int j = 0; j<height;j++){
                for(int k = 0; k<depth;k++) {
                    map[i][j][k] = new IntObject(r.nextInt(2));
                }
            }
        }

        for(int x = 0; x< 60;x++) {
            IntObject newMap[][][] = new IntObject[width][height][depth];

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < depth; k++) {
                        newMap[i][j][k] = validateCell(i, j, k);
                    }
                }
            }

            map = newMap;
        }
    }

    private IntObject validateCell(int i, int j, int k){
        if(i == 0) return new IntObject(1);
        if(j == 0) return new IntObject(1);
        if(k == 0) return new IntObject(1);
        if(i == width-1) return new IntObject(1);
        if(j == height-1) return new IntObject(1);
        if(k == depth-1) return new IntObject(1);

        int alive = 0;

        for(int a = -1;a<2;a++){
            for(int b = -1;b<2;b++){
                for(int c = -1;c<2;c++){
                    if(a == 0 && b == 0 && c == 0) break;
                    if(map[i+a][j+b][k+c].v == 1) alive ++;
                }
            }
        }


        if(alive > 15) return new IntObject(1);;
        if(alive < 11) return new IntObject(0);;

        return map[i][j][k];
    }
}
