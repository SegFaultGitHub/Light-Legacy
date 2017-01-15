package Main;

import Maps.Background;
import Maps.Map;
import Maps.Maps;
import Player.Player;
import Textures.Textures;
import Utils.Utils;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Thomas VENNER on 19/08/2016.
 */
public class Handler {
    private enum GameState {
        inTitleScreen, inGame, inEditor
    }

    private static GameState gameState;
    private static Map map;
    public static Player player;
    private static Vector2f screenSize;
    private static Vector2f offset;
    private static ArrayList<Long> frames;
    private static ArrayList<Integer> previousFPS;

    private static long previousUpdate;

    private static int currentFPS;

    public static Vector2f getOffset() {
        return offset;
    }

    public static void initialize(int screenWidth, int screenHeight) {
        gameState = GameState.inGame;
        map = Maps.getMap("Map test 1");
        player = new Player();
        player.setMapPosition(1, 1);
        screenSize = new Vector2f(screenWidth, screenHeight);
        offset = Vector2f.ZERO;
        frames = new ArrayList<>();
        previousFPS = new ArrayList<>();
        previousUpdate = new Date().getTime();
    }

    public static void update() {
        switch (gameState) {
            case inGame:
                updateInGame();
                break;
            case inTitleScreen:
                updateInTitleScreen();
                break;
            default:
                break;
        }

        //<editor-fold desc="Debug">
        if (Config.DEBUG) {
            long now = new Date().getTime();
            frames.add(now);
            while (now - frames.get(0) > 1000) {
                frames.remove(0);
            }
            currentFPS = frames.size();
            previousFPS.add(currentFPS);
            if (previousFPS.size() > 512) {
                previousFPS.remove(0);
            }
        }
        //</editor-fold>
    }

    private static void updateInGame() {
        long end;
        if (Config.HD) {
            if (Config.PRINT_LAG) {
                Utils.startChrono();
            }
            Background.update(screenSize);
            if (Config.PRINT_LAG) {
                end = Utils.endChrono();
                if (end > 5) {
                    System.out.println("Background: " + end);
                }
            }
        }
        if (Config.PRINT_LAG) {
            Utils.startChrono();
        }
        Map.SEE = 0;
        Map.CELLS_UPDATED = 0;
        map.update(player, offset, screenSize);
//        System.out.println("Map.SEE = " + Map.SEE);
        if (Config.PRINT_LAG) {
            end = Utils.endChrono();
            if (end > 5) {
                System.out.println("Map:        " + end);
            }
        }
        if (Config.PRINT_LAG) {
            Utils.startChrono();
        }
        player.update(map);
        if (Config.PRINT_LAG) {
            end = Utils.endChrono();
            if (end > 5) {
                System.out.println("Player:     " + end);
            }
        }
        offset = Vector2f.sub(
                Vector2f.div(screenSize, 2),
                Vector2f.sub(
                        player.getPosition(),
                        new Vector2f(Player.SIZE / 2, Player.SIZE / 2)
                )
        );
    }

    private static void updateInTitleScreen() {

    }

    public static void draw(RenderWindow window, RenderWindow windowDebug) {
        window.clear(Color.WHITE);

        switch (gameState) {
            case inGame:
                drawInGame(window);
                break;
            case inTitleScreen:
                drawInTitleScreen(window);
                break;
        }

        window.display();

        //<editor-fold desc="Debug">
        if (Config.DEBUG && windowDebug != null) {
            RectangleShape rectangleShape = new RectangleShape();
            windowDebug.clear(Color.WHITE);
            Text text = new Text();
            text.setCharacterSize(18);
            text.setFont(Textures.getFontDebug());
            text.setColor(Color.BLACK);
            String debugString = "FPS: " + String.valueOf(currentFPS) + "\n";
            debugString += "Offset: " + Handler.getOffset() + "\n";
            debugString += "Cells updated: " + Map.CELLS_UPDATED + "\n";
            debugString += Handler.player.dump();
            text.setString(debugString);
            windowDebug.draw(text);

            for (int i = 0; i < previousFPS.size(); i++) {
                rectangleShape.setSize(new Vector2f(1, 1));
                rectangleShape.setFillColor(Color.BLACK);
                rectangleShape.setPosition(i, 256 - previousFPS.get(i));
                windowDebug.draw(rectangleShape);
            }

            windowDebug.display();
        }
        //</editor-fold>
    }

    private static void drawInGame(RenderWindow window) {
        RectangleShape rectangleShape;
        if (Config.HD) {
            Background.draw(window, offset, screenSize);
        }

        //<editor-fold desc="Black filling">
        if (Config.DRAW_BLACK_FILLING) {
            rectangleShape = new RectangleShape();
            float x = Math.max(0, offset.x);
            float y = Math.max(0, offset.y);
            rectangleShape.setSize(new Vector2f(screenSize.x, y));
            rectangleShape.setFillColor(Color.BLACK);
            window.draw(rectangleShape);
            rectangleShape.setSize(new Vector2f(x, screenSize.y));
            rectangleShape.setFillColor(Color.BLACK);
            window.draw(rectangleShape);
            x = Math.max(0, screenSize.x - offset.x - map.getWidth() * Map.SIZE);
            y = Math.max(0, screenSize.y - offset.y - map.getHeight() * Map.SIZE);
            rectangleShape.setSize(new Vector2f(x, screenSize.y));
            rectangleShape.setPosition(new Vector2f(screenSize.x - x, 0));
            rectangleShape.setFillColor(Color.BLACK);
            window.draw(rectangleShape);
            rectangleShape.setSize(new Vector2f(screenSize.x, y));
            rectangleShape.setPosition(new Vector2f(0, screenSize.y - y));
            rectangleShape.setFillColor(Color.BLACK);
            window.draw(rectangleShape);
        }
        //</editor-fold>

        map.drawFirstLayer(window, offset, screenSize);
        player.draw(window, offset);
        map.drawRemainingLayers(window, offset, screenSize);
    }

    private static void drawInTitleScreen(RenderWindow window) {

    }
}

