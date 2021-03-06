import com.google.gson.Gson;
import org.joml.Random;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.*;


public class Terrain {

    private ShaderProgram sh;

    private TerrainChunk[][][] chunks;
    private VoxelObject[][][] map;

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

        this.sh = sh;

        System.out.println("generating terrain...");

        width = (widthChunks*chunkSize)+1;
        height = (heightChunks*chunkSize)+1;
        depth = (depthChunks*chunkSize)+1;

        chunks = new TerrainChunk[widthChunks][heightChunks][depthChunks];

        r = new Random(314159);

        System.out.println("generating map...");

        //load("world.save");

        if(map == null) {
            map = new VoxelObject[width][height][depth];
            generateMap();
            generateChunks();

            save("world.save");
        }

    }

    private void generateChunks(){
        for(int i = 0; i<widthChunks;i++){
            for(int j = 0; j<heightChunks;j++){
                for(int k = 0; k<depthChunks;k++) {

                    VoxelObject[][][] arr = new VoxelObject[chunkSize+1][chunkSize+1][chunkSize+1];

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

        if(KeyboardInput.getKey(GLFW.GLFW_KEY_L) == 1){
            load("world.save");
        }
        if(KeyboardInput.getKey(GLFW.GLFW_KEY_K) == 1){
            save("world.save");
        }

        if(MouseInput.getMouseButton(GLFW.GLFW_MOUSE_BUTTON_2) == 1){
            int x = (int)Math.floor(cursor.getPos().x);
            int y = (int)Math.floor(cursor.getPos().y);
            int z = (int)Math.floor(cursor.getPos().z);
            if(map[x][y][z].v == 0) {
                System.out.println("Set Voxel");
                setVoxel(x, y, z, 1);
            }
        }
        if(MouseInput.getMouseButton(GLFW.GLFW_MOUSE_BUTTON_1) == 1){
            int x = (int)Math.floor(cursor.getPos().x);
            int y = (int)Math.floor(cursor.getPos().y);
            int z = (int)Math.floor(cursor.getPos().z);
            if(map[x][y][z].v == 1) {
                System.out.println("Unset Voxel");
                setVoxel(x, y, z, 0);
            }
        }

        if(cursorPos.x < 0.1) cursorPos.x = 0.1f;
        if(cursorPos.y < 0.1) cursorPos.y = 0.1f;
        if(cursorPos.z < 0.1) cursorPos.z = 0.1f;

        //cursor.setPos(new Vector3f((float)Math.floor(cursorPos.x),(float)Math.floor(cursorPos.y),(float)Math.floor(cursorPos.z)));
    }

    public void setVoxel(int x, int y, int z, int val){
        map[x][y][z].v = val;
        map[x][y][z].color = new Vector3f(0,0.5f,0.5f);

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

        //cursor.render();
    }

    private void generateMap(){
        for(int i = 0; i<width;i++){
            for(int j = 0; j<height;j++){
                for(int k = 0; k<depth;k++) {
                    map[i][j][k] = new VoxelObject(r);
                    float x = r.nextFloat()>0.95f?0.5f:1f;
                    map[i][j][k].color = new Vector3f(x,(float) j/height,0);
                }
            }
        }

        for(int x = 0; x< 60;x++) {
            VoxelObject newMap[][][] = new VoxelObject[width][height][depth];

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

    private VoxelObject validateCell(int i, int j, int k){
        if(i == 0) return new VoxelObject(1,map[i][j][k].color);
        if(j == 0) return new VoxelObject(1,map[i][j][k].color);
        if(k == 0) return new VoxelObject(1,map[i][j][k].color);
        if(i == width-1) return new VoxelObject(1,map[i][j][k].color);
        if(j == height-1) return new VoxelObject(1,map[i][j][k].color);
        if(k == depth-1) return new VoxelObject(1,map[i][j][k].color);

        int alive = 0;

        for(int a = -1;a<2;a++){
            for(int b = -1;b<2;b++){
                for(int c = -1;c<2;c++){
                    if(a == 0 && b == 0 && c == 0) break;
                    if(map[i+a][j+b][k+c].v == 1) alive ++;
                }
            }
        }


        if(alive > 15) return new VoxelObject(1,map[i][j][k].color);;
        if(alive < 11) return new VoxelObject(0,map[i][j][k].color);;

        return map[i][j][k];
    }

    public void save(String save){
        Gson gson = new Gson();
        String json = gson.toJson(map);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/saves/" + save));

            bw.write(json);
            bw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void load(String save){
        Gson gson = new Gson();

        map = null;

        StringBuilder contentBuilder = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/saves/" + save));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
            map = gson.fromJson(contentBuilder.toString(), VoxelObject[][][].class);
            generateChunks();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
