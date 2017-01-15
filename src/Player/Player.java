package Player;

import Utils.Utils;
import Utils.Input;
import Maps.Map;
import Textures.Textures;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

/**
 * Created by Thomas VENNER on 22/08/2016.
 */
public class Player {
    public static float SIZE = 16f; //24f;
    private static Color[] COLORS = {
            new Color(255, 0, 0),
            new Color(255, 255, 0),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(0, 0, 255),
            new Color(255, 0, 255)
    };
    private static Vector2f GRAVITY_AIR = new Vector2f(0, 0.23f);
    private static Vector2f JUMP_FORCE = new Vector2f(0, -5.5f);
    private static float MAX_SPEED = SIZE - 1;
    private static float MAX_SPEED_MOVEMENT = 2.5f;
    private static float ACCELERATION = 0.3f;
    private static boolean small = false;
    private static int jumps = 0;

    @Deprecated
    public Vector2f getSpeed() {
        return speed;
    }

    private Vector2f position;
    private Vector2f speed;

    public Vector2f getPosition() {
        return position;
    }

    private int colorIndex;
    private Color color;

    public Player() {
        color = COLORS[0];
        colorIndex = 1;
        position = new Vector2f(0, 0);
        speed = new Vector2f(0, 0);
    }

    public void setMapPosition(int x, int y) {
        x = Map.SIZE * x;
        y = Map.SIZE * y;
        x += (Map.SIZE - SIZE) / 2;
        y += Map.SIZE - SIZE;
        position = new Vector2f(x, y);
    }

    public void update(Map map) {
        //<editor-fold desc="Colors">
        int r = COLORS[colorIndex].r - color.r;
        int g = COLORS[colorIndex].g - color.g;
        int b = COLORS[colorIndex].b - color.b;
        if (r != 0) r = r / Math.abs(r);
        if (g != 0) g = g / Math.abs(g);
        if (b != 0) b = b / Math.abs(b);
        r += color.r;
        g += color.g;
        b += color.b;
        color = new Color(Math.max(Math.min(255, r), 0),
                Math.max(Math.min(255, g), 0),
                Math.max(Math.min(255, b), 0));
        if (color.r == COLORS[colorIndex].r &&
                color.g == COLORS[colorIndex].g &&
                color.b == COLORS[colorIndex].b) {
            colorIndex += 1;
            colorIndex %= COLORS.length;
        }
        //</editor-fold>
        //<editor-fold desc="Collision & Input">
        if (speed.y == 0 && (position.y + SIZE) % Map.SIZE == 0) {
            jumps = 0;
        }
        speed = new Vector2f(speed.x + GRAVITY_AIR.x, speed.y + GRAVITY_AIR.y);
        float speedLength = Utils.vector2fLength(speed);
        if (Input.isKeyPressed(Keyboard.Key.LEFT)) {
            if (speed.x > -MAX_SPEED_MOVEMENT) {
                speed = Vector2f.add(speed, new Vector2f(-ACCELERATION, 0));
            }
        } else if (Input.isKeyPressed(Keyboard.Key.RIGHT)) {
            if (speed.x < MAX_SPEED_MOVEMENT) {
                speed = Vector2f.add(speed, new Vector2f(ACCELERATION, 0));
            }
        } else {
            if (Math.abs(speed.x) < ACCELERATION) {
                speed = new Vector2f(0, speed.y);
            }
            else if (speed.x > 0) {
                speed = Vector2f.add(speed, new Vector2f(-ACCELERATION, 0));
            } else if (speed.x < 0) {
                speed = Vector2f.add(speed, new Vector2f(ACCELERATION, 0));
            }
        }
        if (speedLength > MAX_SPEED) {
            speed = Vector2f.mul(Vector2f.div(speed, speedLength), MAX_SPEED);
        }
        if (!moveX(map)) {
            speed = new Vector2f(0, speed.y);
        }
        if (!moveY(map)) {
            speed = new Vector2f(speed.x, 0);
        }
        if (Input.isKeyPressedOnce(Keyboard.Key.UP)) {
            if (speed.y == 0 && (position.y + SIZE) % Map.SIZE == 0) {
                jump();
                jumps = 0;
            } else if (speed.y != 0 && jumps == 0) {
                jump();
                jumps++;
            }
        }
        //</editor-fold>
    }

    private boolean moveX(Map map) {
        for (int x = 0; x < Math.abs(speed.x); x++) {
            if (speed.x > 0) {
                int newX = (int)(position.x + SIZE + 1f);
                for (int y = (int)position.y; y < position.y + SIZE; y += small ? 1 : 4) {
                    if (!map.isWalkable(newX - 1, y)) {
                        return false;
                    }
                }
                position = new Vector2f(position.x + 1, position.y);
            } else {
                int newX = (int)((position.x - 1f));
                for (int y = (int)position.y; y < position.y + SIZE; y += small ? 1 : 4) {
                    if (!map.isWalkable(newX, y)) {
                        return false;
                    }
                }
                position = new Vector2f(position.x - 1, position.y);
            }
        }
        return true;
    }

    private boolean moveY(Map map) {
        for (int y = 0; y < Math.abs(speed.y); y++) {
            if (speed.y > 0) {
                int newY = (int)(position.y + SIZE + 1f);
                for (int x = (int)position.x; x < position.x + SIZE; x += small ? 1 : 4) {
                    if (!map.isWalkable(x, newY - 1)) {
                        return false;
                    }
                }
                position = new Vector2f(position.x, position.y + 1);
            } else {
                int newY = (int)(position.y - 1f);
                for (int x = (int)position.x; x < position.x + SIZE; x += small ? 1 : 4) {
                    if (!map.isWalkable(x, newY)) {
                        return false;
                    }
                }
                position = new Vector2f(position.x, position.y - 1);
            }
        }
        return true;
    }

    private void jump() {
        speed = new Vector2f(speed.x, 0);
        speed = Vector2f.add(speed, JUMP_FORCE);
    }

    public void draw(RenderWindow window, Vector2f offset) {
        Sprite sprite = new Sprite();
        sprite.setTexture(Textures.getPixel());
        sprite.setScale(SIZE, SIZE);
        sprite.setColor(Color.BLACK);
        sprite.setPosition(Vector2f.add(position, offset));
        window.draw(sprite);
        sprite.setColor(color);
        sprite.setScale(SIZE - 6, SIZE - 6);
        sprite.setPosition(new Vector2f(position.x + 3 + offset.x, position.y + 3 + offset.y));
        window.draw(sprite);
    }

    public String dump() {
        String result = "";
        result += "vector speed: " + speed.toString() + "\n";
        result += "speed length: " + String.valueOf(Utils.vector2fLength(speed)) + "\n";
        result += "position: " + position.toString() + "\n";
        result += "map position: " + Vector2f.div(position, SIZE) + "\n";
        result += "color: " + color.toString() + "\n";
        result += "jumps: " + jumps;
        return result;
    }
}
