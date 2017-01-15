package Maps;

import Main.Config;
import Player.Player;
import Utils.Utils;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;

/**
 * Created by Thomas VENNER on 20/08/2016.
 */
public class Map {
    public static int SEE = 0;
    public static int CELLS_UPDATED = 0;

    private static boolean tooLong = false;
    public static int SIZE = 24; //32;
    private static float VIEW_SIZE = 5f;
    private static float TORCH_SIZE = 4f;

    private Cell[][] cells;
    private float[][] goalVisibility;
    private float[][] actualVisibility;
    private int width, height;
    private boolean[][] cellsUpdated;
    private ArrayList<Vector2f> lazersPositions;
    private ArrayList<Vertex[]> lazersVertices;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map(Cell[][] cells, int width, int height) {
        this.cells = cells;
        this.lazersVertices = new ArrayList<>();
        this.lazersPositions = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.cellsUpdated = new boolean[width][height];
        this.goalVisibility = new float[width][height];
        this.actualVisibility = new float[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (cells[i][j].getContent()[0] == Cell.LAZER) {
                    lazersPositions.add(new Vector2f(i, j));
                }
                actualVisibility[i][j] = 0;
                goalVisibility[i][j] = 0;
            }
        }
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= width * SIZE || y >= height * SIZE)
            return true;
        return cells[x / SIZE][y / SIZE].isWalkable();
    }

    public void update(Player player, Vector2f offset, Vector2f screenSize) {
        if (tooLong) {
            System.out.println("#---#");
        }
        tooLong = false;
        float a = 0.03f;
        Vector2f playerPosition = Vector2f.add(player.getPosition(), new Vector2f(Player.SIZE / 2, Player.SIZE / 2));
        goalVisibility = new float[width][height];
        int minX = Math.max(-(int)offset.x / SIZE, 0);
        int maxX = Math.min(width, ((int)screenSize.x - (int)offset.x) / SIZE + 1);
        int minY = Math.max(-(int)offset.y / SIZE, 0);
        int maxY = Math.min(height, ((int)screenSize.y - (int)offset.y) / SIZE + 1);
        this.cellsUpdated = new boolean[width][height];
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                CELLS_UPDATED++;
                cells[i][j].update();
                cellsUpdated[i][j] = true;
                if (Config.COMPUTE_VISIBILITY) {
                    Utils.startChrono();
                    float v = visibility(playerPosition.x, playerPosition.y, i, j, VIEW_SIZE);
                    goalVisibility[i][j] = Math.max(goalVisibility[i][j], v);
                    long end = Utils.endChrono();
                    if (Config.PRINT_LAG && end > 10) {
                        tooLong = true;
                        System.out.println(i + ", " + j + ": " + end);
                    }
                    float x = (i + 0.5f) * SIZE;
                    float y = (j + 0.5f) * SIZE;
                    if (cells[i][j].getContent()[0] == Cell.TORCH && see(playerPosition.x, playerPosition.y, x, y)) {
                        for (int ii = (int)Math.max(0, i - TORCH_SIZE); ii < i + TORCH_SIZE && ii < width; ii++) {
                            for (int jj = (int)Math.max(0, j - TORCH_SIZE); jj < j + TORCH_SIZE && jj < height; jj++) {
                                v = visibility(x, y, ii, jj, TORCH_SIZE);
                                goalVisibility[ii][jj] = Math.max(goalVisibility[ii][jj], v);
                            }
                        }
                    }
                }
            }
        }
        lazersVertices.clear();
        for (Vector2f lazerPosition : lazersPositions) {
            int i = (int)lazerPosition.x;
            int j = (int)lazerPosition.y;
            if (!cellsUpdated[i][j]) {
                cells[i][j].update();
            }
        }
        if (Config.COMPUTE_VISIBILITY) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (Math.abs(goalVisibility[i][j] - actualVisibility[i][j]) < a) {
                        actualVisibility[i][j] = goalVisibility[i][j];
                    } else if (goalVisibility[i][j] > actualVisibility[i][j]) {
                        actualVisibility[i][j] += a;
                    } else {
                        actualVisibility[i][j] -= a;
                    }
                }
            }
        }
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= width * SIZE || y < 0 || y >= height * SIZE)
            return null;
        return cells[x / SIZE][y / SIZE];
    }

    public void drawFirstLayer(RenderWindow window, Vector2f offset, Vector2f screenSize) {
        // i * SIZE + offset.x > 0
        // i * SIZE > -offset.x
        // i > -offset.x / SIZE

        // i * SIZE + offset.x < screenWidth
        // i * SIZE < width - offset.x
        // i < (width - offset.x) / SIZE
        int minX = Math.max(-(int)offset.x / SIZE, 0);
        int maxX = Math.min(width, ((int)screenSize.x - (int)offset.x) / SIZE + 1);
        int minY = Math.max(-(int)offset.y / SIZE, 0);
        int maxY = Math.min(height, ((int)screenSize.y - (int)offset.y) / SIZE + 1);
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                Cell cell = getCell(i * SIZE, (j - 1) * SIZE);
                cells[i][j].drawFirstLayer(window, i * SIZE + (int)offset.x, j * SIZE + (int)offset.y, cell == null ? null : cell.getContent());
            }
        }
    }

    public void drawRemainingLayers(RenderWindow window, Vector2f offset, Vector2f screenSize) {
        RectangleShape rectangleShape = new RectangleShape();
        rectangleShape.setSize(new Vector2f(SIZE, SIZE));
        int minX = Math.max(-(int)offset.x / SIZE, 0);
        int maxX = Math.min(width, ((int)screenSize.x - (int)offset.x) / SIZE + 1);
        int minY = Math.max(-(int)offset.y / SIZE, 0);
        int maxY = Math.min(height, ((int)screenSize.y - (int)offset.y) / SIZE + 1);
        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                Cell cell = getCell(i * SIZE, (j - 1) * SIZE);
                cells[i][j].drawRemainingLayers(window, i * SIZE + (int)offset.x, j * SIZE + (int)offset.y, cell == null ? null : cell.getContent());
                if (Config.COMPUTE_VISIBILITY) {
                    rectangleShape.setPosition(i * SIZE + (int) offset.x, j * SIZE + (int) offset.y);
                    rectangleShape.setFillColor(new Color(0, 0, 0, (int) ((1 - actualVisibility[i][j]) * 255)));
                    window.draw(rectangleShape);
                }
            }
        }
    }

    public boolean see(float xSrc, float ySrc, float xDst, float yDst) {

        if (xDst < 0 || yDst < 0 || xDst >= width * SIZE || yDst >= height * SIZE
                || xSrc < 0 || ySrc < 0 || xSrc >= width * SIZE || ySrc >= height * SIZE)
            return true;

        Vector2f src = new Vector2f(xSrc, ySrc);
        Vector2f dst = new Vector2f(xDst, yDst);
        Vector2f direction = Vector2f.sub(dst, src);
        float distance = Utils.vector2fLength(direction);
        Vector2f current = new Vector2f(xSrc, ySrc);
        System.out.println("src = " + src);
        System.out.println("dst = " + dst);
        while (Utils.vector2fLength(Vector2f.sub(current, src)) < distance) {
            SEE++;
            float x = current.x;
            float y = current.y;
            int xCell = (int)(x / SIZE);
            int yCell = (int)(y / SIZE);
            if ((cells[xCell][yCell]).isOpaque()) {
                return false;
            }
            float nextX;
            float nextY;
            if (direction.x < 0) {
                nextX = (int)(x / SIZE - (x % SIZE == 0 ? 1 : 0)) * SIZE;
            } else {
                nextX = (int)(x / SIZE + 1) * SIZE;
            }
            if (direction.y < 0) {
                nextY = (int)(y / SIZE  - (y % SIZE == 0 ? 1 : 0)) * SIZE;
            } else {
                nextY = (int)(y / SIZE + 1) * SIZE;
            }
            float ratioX = (nextX - x) / direction.x;
            float ratioY = (nextY - y) / direction.y;
            float ratio = Math.min(ratioX, ratioY);
            current = Vector2f.add(current, Vector2f.mul(direction, ratio));
            xCell = (int)(current.x / SIZE);
            yCell = (int)(current.y / SIZE);
            if ((cells[xCell][yCell]).isOpaque()) {
                return false;
            }
        }
        return true;


//        Vector2f src = new Vector2f(xSrc, ySrc);
//        Vector2f dst = new Vector2f(xDst, yDst);
//        Vector2f direction = Vector2f.sub(dst, src);
//        float distance = Utils.vector2fLength(direction);
//        direction = Vector2f.div(direction, distance);
//        Vector2f current = new Vector2f(xSrc, ySrc);
//        while (Utils.vector2fLength(Vector2f.sub(current, src)) < distance) {
//            SEE++;
//            int xCell = (int)(current.x / SIZE);
//            int yCell = (int)(current.y / SIZE);
//            if ((cells[xCell][yCell]).isOpaque()) {
//                return false;
//            }
//            current = Vector2f.add(current, direction);
//        }
//        return true;
    }

    public float visibility(float xSrc, float ySrc, float xCell, float yCell, float viewSize) {
        Vector2f src = new Vector2f(xSrc, ySrc);
        Vector2f dst = Vector2f.mul(new Vector2f(xCell + 0.5f, yCell + 0.5f), SIZE);
        float distance = Utils.vector2fLength(Vector2f.sub(src, dst));
        if (distance > viewSize * SIZE) {
            return 0;
        }
        ArrayList<Vector2f> corners = new ArrayList<>();
        corners.add(Vector2f.mul(new Vector2f(xCell, yCell), SIZE));
        corners.add(Vector2f.mul(new Vector2f(xCell + 1, yCell), SIZE));
        corners.add(Vector2f.mul(new Vector2f(xCell, yCell + 1), SIZE));
        corners.add(Vector2f.mul(new Vector2f(xCell + 1, yCell + 1), SIZE));
        float count = 0;
        for (Vector2f corner : corners) {
            if (see(xSrc, ySrc, corner.x, corner.y)) {
                count++;
            }
        }
        float distanceRate = Math.max(0, (viewSize * (float)SIZE - distance) / (viewSize * (float)SIZE));
        float countRate = count / 4f;
        return distanceRate * countRate;
    }
}