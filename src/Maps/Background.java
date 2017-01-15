package Maps;

import Utils.Utils;
import Utils.Input;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import java.util.ArrayList;

/**
 * Created by Thomas VENNER on 09/10/2016.
 */
public class Background {
    private static boolean gravity;
    private static float gravityFloat;
    private static int height;
    private static ArrayList<RenderTexture> layers;

    public static void initialize(int screenWidth, int screenHeight) {
        gravity = true;
        gravityFloat = 2f;
        height = 0;
        layers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            RenderTexture texture = new RenderTexture();
            try {
                texture.create(screenWidth, screenHeight);
                layers.add(texture);
            } catch (TextureCreationException e) {
                e.printStackTrace();
            }
        }
        int count = screenWidth * screenHeight / 300;
        for (int i = 0; i < count; i++) {
            int x = Utils.getRandomInt(screenWidth);
            int y = Utils.getRandomInt(screenHeight);
            int depth = Utils.getRandomInt(1, 10);
            RectangleShape rectangleShape = new RectangleShape();
            rectangleShape.setSize(new Vector2f(2, 2));
            rectangleShape.setPosition(new Vector2f(x, y));
            rectangleShape.setFillColor(new Color((10 - depth) * 20, (10 - depth) * 20, (10 - depth) * 20));
            layers.get(depth - 1).draw(rectangleShape);
            layers.get(depth - 1).display();
        }
    }

    public static void update(Vector2f screenSize) {
        height += gravityFloat;
        if (gravity && gravityFloat < 2f) {
            gravityFloat += 0.1f;
        } else if (!gravity && gravityFloat > -2f) {
            gravityFloat -= 0.1f;
        }
        if (Input.isKeyPressedOnce(Keyboard.Key.A)) {
            gravity = !gravity;
        }
//        int size = elements.size();
//        for (int i = 0; i < elements.size(); i++) {
//            if (!elements.get(i).update()) {
//                elements.remove(i);
//                i--;
//            }
//        }
//        while (elements.size() < size) {
//            elements.add(new Element(Utils.getRandomInt((int)screenSize.x), (int)screenSize.y));
//        }
    }

    public static void draw(RenderWindow window, Vector2f offset, Vector2f screenSize) {
        int n = 10;
        for (RenderTexture texture : layers) {
            float x = (((offset.x / n) % screenSize.x) + screenSize.x) % screenSize.x;
            float y = (height / n) % screenSize.y;
            Sprite sprite = new Sprite();
            sprite.setTexture(texture.getTexture());
            sprite.setPosition(x, -y);
            window.draw(sprite);
            sprite.setPosition(x, -y + screenSize.y);
            window.draw(sprite);
            sprite.setPosition(x - screenSize.x, -y);
            window.draw(sprite);
            sprite.setPosition(x - screenSize.x, -y + screenSize.y);
            window.draw(sprite);
            n--;
        }
    }
}
