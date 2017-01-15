package Maps;

import Textures.Textures;
import Utils.Utils;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

/**
 * Created by Thomas VENNER on 20/08/2016.
 */
public class Cell {
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int WATER = 2;
    public static final int ACID = 3;
    public static final int TORCH = 4;
    public static final int LAZER = 5;
    private static int SMALLER_SIZE = 6;

    private int[] content;
    public int[] getContent() {
        return content;
    }

    private int[] innerVar;

    public Cell(int[] content) {
        this.content = content.clone();
        innerVar = new int[5];
        switch (content[0]) {
            case WATER:
            case ACID:
                innerVar[0] = 200;
                innerVar[1] = Utils.getRandomInt(1, 4) * (Utils.getRandomBoolean() ? 1 : -1);
                break;
            case LAZER:
                innerVar[0] = 0; // Min angle
                innerVar[1] = 360; // Max angle
                innerVar[2] = 0; // Current angle
                break;
            default:
                break;
        }
    }

    public boolean isWalkable() {
        switch (content[0]) {
            case WALL:
                return false;
            default:
                break;
        }
        return true;
    }

    public boolean isOpaque() {
        switch (content[0]) {
            case 1:
                return true;
            default:
                break;
        }
        return false;
    }

    public void update() {
        switch (content[0]) {
            default:
                break;
        }
        switch (content[1]) {
            case ACID:
            case WATER:
                innerVar[0] += innerVar[1];
                if (innerVar[1] > 0) {
                    if (innerVar[0] >= 255) {
                        innerVar[0] = 255;
                        innerVar[1] = -Utils.getRandomInt(1, 4);
                    }
                } else {
                    if (innerVar[0] < 128) {
                        innerVar[0] = 128;
                        innerVar[1] = Utils.getRandomInt(1, 4);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void drawFirstLayer(RenderWindow window, int x, int y, int[] contentAbove) {
        RectangleShape rectangleShape = new RectangleShape();
        rectangleShape.setPosition(x, y);
        rectangleShape.setSize(new Vector2f(Map.SIZE, Map.SIZE));
        switch (content[0]) {
            case WALL:
                rectangleShape.setTexture(Textures.getPixel());
                rectangleShape.setFillColor(Color.BLACK);
                break;
            default:
                return;
        }
        window.draw(rectangleShape);
    }

    public void drawRemainingLayers(RenderWindow window, int x, int y, int[] contentAbove) {
        for (int i = 1; i < content.length; i++) {
            RectangleShape rectangleShape = new RectangleShape();
            boolean smaller = false;
            switch (content[i]) {
                case WALL:
                    rectangleShape.setPosition(x, y);
                    rectangleShape.setSize(new Vector2f(Map.SIZE, Map.SIZE));
                    rectangleShape.setTexture(Textures.getPixel());
                    rectangleShape.setFillColor(Color.BLACK);
                    break;
                case ACID:
                    if (contentAbove != null && contentAbove.length >= i) {
                        smaller = contentAbove[i] != content[i];
                    }
                    rectangleShape.setPosition(x, y + (smaller ? SMALLER_SIZE : 0));
                    rectangleShape.setSize(new Vector2f(Map.SIZE, Map.SIZE + (smaller ? -SMALLER_SIZE : 0)));
                    rectangleShape.setTexture(Textures.getPixel());
                    rectangleShape.setFillColor(new Color(0, innerVar[0], 0, 128));
                    break;
                case WATER:
                    if (contentAbove != null && contentAbove.length > i) {
                        smaller = contentAbove[i] != content[i];
                    }
                    rectangleShape.setPosition(x, y + (smaller ? SMALLER_SIZE : 0));
                    rectangleShape.setSize(new Vector2f(Map.SIZE, Map.SIZE + (smaller ? -SMALLER_SIZE : 0)));
                    rectangleShape.setTexture(Textures.getPixel());
                    rectangleShape.setFillColor(new Color(0, 0, innerVar[0], 128));
                    break;
                case LAZER:
                    rectangleShape.setPosition(x, y);
                    rectangleShape.setSize(new Vector2f(Map.SIZE, Map.SIZE));
                    rectangleShape.setTexture(Textures.getPixel());
                    rectangleShape.setFillColor(new Color(255, 0, 255, 128));
                    break;
                default:
                    break;
            }
            window.draw(rectangleShape);
        }
    }

    @Override
    public Cell clone() {
        int length = this.content.length;
        int[] content = new int[length];
        for (int i = 0; i < length; i++) {
            content[i] = this.content[i];
        }
        return new Cell(content);
    }
}
