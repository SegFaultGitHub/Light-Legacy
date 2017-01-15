package Utils;

import org.jsfml.system.Vector2f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Date;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Thomas VENNER on 20/08/2016.
 */
public class Utils {
    private static Stack<Long> timeStack = new Stack<>();
    private static Random random = new Random();

    /* max excluded */
    public static int getRandomInt(int max) {
        return random.nextInt(max);
    }

    /* min included, max excluded */
    public static int getRandomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public static void startChrono() {
        timeStack.push(new Date().getTime());
    }

    public static long endChrono() {
        if (!timeStack.empty()) {
            return new Date().getTime()- timeStack.pop();
        }
        return -1;
    }

    public static JSONObject readJSON(String path) {
        JSONParser jsonParser = new JSONParser();
        Object obj = null;

        try {
            obj = jsonParser.parse(new FileReader(path));
        } catch (Exception e) {
            return null;
        }

        return (JSONObject) obj;
    }

    public static String stringRepeat(String s, int n) {
        n = Math.max(0, n);
        return new String(new char[n]).replace("\0", s);
    }

    public static String reformatJSON(JSONObject jsonObject, int tab) {
        String result = "{\n";
        boolean first = true;
        for (Object obj : jsonObject.keySet()) {
            if (!first) {
                result += ",\n";
            }
            first = false;
            String key = obj.toString();
            Object value = jsonObject.get(key);
            result += stringRepeat(" ", 4 * (tab + 1)) + "\""+ key + "\" : ";
            if (value.getClass() == JSONObject.class) {
                result += reformatJSON((JSONObject) value, tab + 1);
            } else if (value.getClass() == JSONArray.class) {

            } else if (value.getClass() == String.class) {
                result += "\"" + value.toString() + "\"";
            } else {
                result += value.toString();
            }
        }
        result += "\n" + stringRepeat(" ", tab * 4) + "}";
        return result;
    }

    public static float vector2fLength(Vector2f vector2f) {
        if (vector2f == null)
            return -1;
        return (float)Math.sqrt(vector2f.x * vector2f.x + vector2f.y * vector2f.y);
    }
}
