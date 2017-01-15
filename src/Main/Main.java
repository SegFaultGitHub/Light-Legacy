package Main;

import Maps.Background;
import Maps.Maps;
import Textures.Textures;
import Utils.Input;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

/**
 * Created by Thomas VENNER on 19/08/2016.
 */
public class Main {
    private static RenderWindow window;
    private static RenderWindow windowDebug;

    private static void initialize() throws Exception {
        Config.initialize();
        windowDebug = null;
        if (Config.DEBUG) {
            windowDebug = new RenderWindow();
            windowDebug.create(new VideoMode(512, 256, 32), "Light debug", WindowStyle.RESIZE);
            windowDebug.setFramerateLimit(60);
        }
        window = new RenderWindow();
        if (Config.FULL_SCREEN) {
            window.create(VideoMode.getDesktopMode(), "Light", WindowStyle.FULLSCREEN);
        } else {
            window.create(new VideoMode(640, 480, 32), "Light", WindowStyle.RESIZE);
        }
        window.setFramerateLimit(60);
        Maps.initialize(window.getSize().x, window.getSize().y);
        Textures.initialize();
        Input.initialize();
        Background.initialize(window.getSize().x, window.getSize().y);
        Handler.initialize(window.getSize().x, window.getSize().y);
    }

    private static void update() {
        Input.update();
        Handler.update();
    }

    private static void draw() {
        Handler.draw(window, windowDebug);
    }

    public static void main(String[] args) throws Exception {
        initialize();

        while (window.isOpen()) {
            update();
            draw();
            window.pollEvent();
        }
    }
}
