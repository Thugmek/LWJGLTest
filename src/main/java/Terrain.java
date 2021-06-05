import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class Terrain {

    private TerrainChunk[][][] chunks;
    private IntObject[][][] map;

    private Model cursor;
    private Vector3f cursorPos;

    int widthChunks = 5;
    int heightChunks = 5;
    int depthChunks = 5;

    int width;
    int height;
    int depth;

    int chunkSize = 16;

    Random r;

    public Terrain(ShaderProgram sh, Vector3f translate){
        System.out.println("generating terrain...");

        width = (widthChunks*chunkSize)+1;
        height = (heightChunks*chunkSize)+1;
        depth = (depthChunks*chunkSize)+1;

        map = new IntObject[width][height][depth];
        chunks = new TerrainChunk[widthChunks][heightChunks][depthChunks];

        r = new Random(314159);

        System.out.println("generating map...");

        generateMap();

        for(int i = 0; i<widthChunks;i++){
            for(int j = 0; j<heightChunks;j++){
                for(int k = 0; k<depthChunks;k++) {

                    IntObject[][][] arr = new IntObject[chunkSize+1][chunkSize+1][chunkSize+1];

                    for(int x = 0; x<chunkSize+1;x++){
                        for(int y = 0; y<chunkSize+1;y++){
                            for(int z = 0; z<chunkSize+1;z++) {
                                arr[x][y][z] = map[x + i*chunkSize][y + j*chunkSize][z + k*chunkSize];

                            }
                        }
                    }

                    chunks[i][j][k] = new TerrainChunk(arr, new Vector3f(i*chunkSize,j*chunkSize,k*chunkSize), sh);

                }
            }
        }

    }

    public void setCursor(Model cursor){
        this.cursor = cursor;
        cursorPos = cursor.getPos();
    }

    public void update(float delta){
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_UP) == 1){
            cursorPos.add(new Vector3f(0,0,delta));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_DOWN) == 1){
            cursorPos.add(new Vector3f(0,0,-delta));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_LEFT) == 1){
            cursorPos.add(new Vector3f(delta,0,0));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_RIGHT) == 1){
            cursorPos.add(new Vector3f(-delta,0,0));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_PAGE_UP) == 1){
            cursorPos.add(new Vector3f(0,delta,0));
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_PAGE_DOWN) == 1){
            cursorPos.add(new Vector3f(0,-delta,0));
        }

        if(KeyboardInput.getKey(GLFW.GLFW_KEY_RIGHT_SHIFT) == 1){
            int x = (int)Math.floor(cursorPos.x);
            int y = (int)Math.floor(cursorPos.y);
            int z = (int)Math.floor(cursorPos.z);
            if(map[x][y][z].v == 0) {
                System.out.println("Set Voxel");
                setVoxel(x, y, z, 1);
            }
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_ENTER) == 1){
            int x = (int)Math.floor(cursorPos.x);
            int y = (int)Math.floor(cursorPos.y);
            int z = (int)Math.floor(cursorPos.z);
            if(map[x][y][z].v == 1) {
                System.out.println("Unset Voxel");
                setVoxel(x, y, z, 0);
            }
        }

        if(cursorPos.x < 0.1) cursorPos.x = 0.1f;
        if(cursorPos.y < 0.1) cursorPos.y = 0.1f;
        if(cursorPos.z < 0.1) cursorPos.z = 0.1f;

        cursor.setPos(new Vector3f((float)Math.floor(cursorPos.x),(float)Math.floor(cursorPos.y),(float)Math.floor(cursorPos.z)));
    }

    public void setVoxel(int x, int y, int z, int val){
        map[x][y][z].v = val;

        int chunkX = x/chunkSize;
        int chunkY = y/chunkSize;
        int chunkZ = z/chunkSize;

        chunks[chunkX][chunkY][chunkZ].recalculate();

        if(x%chunkSize == 0 && x>0) chunks[chunkX-1][chunkY][chunkZ].recalculate();
        if(y%chunkSize == 0 && y>0) chunks[chunkX][chunkY-1][chunkZ].recalculate();
        if(z%chunkSize == 0 && z>0) chunks[chunkX][chunkY][chunkZ-1].recalculate();

    }

    public void render(){

        for(int i = 0; i<widthChunks;i++){
            for(int j = 0; j<heightChunks;j++){
                for(int k = 0; k<depthChunks;k++) {
                    chunks[i][j][k].render();
                }
            }
        }

        cursor.render();
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
