package Utils;

import org.jsfml.window.Keyboard;

import java.util.HashMap;

/**
 * Created by Thomas VENNER on 19/08/2016.
 */
public class Input {
    public static HashMap<Keyboard.Key, Boolean> previousKeys = new HashMap<>();
    public static HashMap<Keyboard.Key, Boolean> currentKeys = new HashMap<>();

    public static void initialize() {
        Keyboard.Key[] keys = Keyboard.Key.values();
        for (Keyboard.Key key : keys) {
            previousKeys.put(key, false);
            currentKeys.put(key, false);
        }
    }

    public static void update() {
        Keyboard.Key[] keys = Keyboard.Key.values();
        for (int i = 1; i < keys.length; i++) {
            Keyboard.Key key = keys[i];
            previousKeys.put(key, currentKeys.get(key));
            currentKeys.put(key, Keyboard.isKeyPressed(key));
        }
    }

    public static boolean isKeyPressed(Keyboard.Key key) {
        return currentKeys.get(key);
    }

    public static boolean isKeyPressedOnce(Keyboard.Key key) {
        return currentKeys.get(key) && !previousKeys.get(key);
    }
}

