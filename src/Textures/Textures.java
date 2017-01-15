package Textures;

import org.jsfml.graphics.*;

import java.nio.file.Paths;

/**
 * Created by Thomas VENNER on 19/08/2016.
 */
public class Textures {
    private static Font font, fontDebug;
    private static Texture windowskin;

    public static Texture getPixel() {
        return pixel;
    }

    private static Texture pixel;

    public static Font getFontDebug() {
        return fontDebug;
    }

    public static Font getFont() {
        return font;
    }

    public static void initialize() throws TextureCreationException {
        font = new Font();
        try {
            font.loadFromFile(Paths.get("graphics/fonts/Calibri.ttf", new String[0]));
        } catch (Exception e) {
            System.err.println("Cannot load font Calibri.");
        }
        fontDebug = new Font();
        try {
            fontDebug.loadFromFile(Paths.get("graphics/fonts/DEC Terminal Modern.ttf", new String[0]));
        } catch (Exception e) {
            System.err.println("Cannot load font DEC Terminal Modern.");
        }

        windowskin = new Texture();
        try {
            windowskin.loadFromFile(Paths.get("graphics/windowskin.png", new String[0]));
        } catch (Exception e) {
            System.err.println("Cannot load texture windowskin.");
        }

        pixel = new Texture();
        Image pixelImage = new Image();
        pixelImage.create(1, 1, Color.WHITE);
        pixel.loadFromImage(pixelImage);
    }
}
