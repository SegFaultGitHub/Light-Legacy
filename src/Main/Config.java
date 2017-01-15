package Main;

import Utils.Utils;
import org.json.simple.JSONObject;

/**
 * Created by Thomas VENNER on 24/10/2016.
 */
public class Config {
    public static boolean FULL_SCREEN = true;
    public static boolean HD = true;
    public static boolean COMPUTE_VISIBILITY = true;
    public static boolean DEBUG = false;
    public static boolean PRINT_LAG = false;
    public static boolean DRAW_BLACK_FILLING = true;

    public static void initialize() {
        JSONObject jsonObject = Utils.readJSON("config/config.json");
        HD = (long) jsonObject.get("HD") != 0;
        FULL_SCREEN = (long) jsonObject.get("FULL_SCREEN") != 0;
        COMPUTE_VISIBILITY = (long) jsonObject.get("COMPUTE_VISIBILITY") != 0;
        DEBUG = (long) jsonObject.get("DEBUG") != 0;
        if (DEBUG) {
            PRINT_LAG = (long) jsonObject.get("PRINT_LAG") != 0;
        }
        DRAW_BLACK_FILLING = (long) jsonObject.get("DRAW_BLACK_FILLING") != 0;
    }
}
