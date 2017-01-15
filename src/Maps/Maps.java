package Maps;

import Utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Thomas VENNER on 20/08/2016.
 */
public class Maps {
    private static HashMap<String, Map> maps;

    public static HashMap<String, Map> getMaps() {
        return maps;
    }

    public static void initialize(int screenWidth, int screenHeight) throws Exception {
        maps = new HashMap<>();
        JSONObject jsonObject = Utils.readJSON("datas/maps.json");

        for (Object obj : (JSONArray) jsonObject.get("maps")) {
            JSONObject mapJson = (JSONObject) obj;
            String name = mapJson.get("name").toString();
            long width = (long) mapJson.get("width");
            long height = (long) mapJson.get("height");

            Cell[][] cells = new Cell[(int) width][(int) height];
            JSONArray contentJson = (JSONArray) mapJson.get("content");
            JSONObject subcontentJson = (JSONObject) mapJson.get("subcontent");
            for (int j = 0; j < height; j++) {
                String[] line = contentJson.get(j).toString().split(" +");
                for (int i = 0; i < width; i++) {
                    if (line[i].startsWith("s")) {
//                        JSONArray subcontentArray = (JSONArray)subcontentJson.get(line[i]);
//                        Cell[][] subcontentCells = new Cell[8][8];
//                        for (int jj = 0; jj < 8; jj++) {
//                            String[] line2 = subcontentArray.get(jj).toString().split(" +");
//                            for (int ii = 0; ii < 8; ii++) {
//                                List<String> contentStrings = Arrays.asList(line2[ii].split("/"));
//                                int[] content = new int[3];
//                                int n = 0;
//                                for (String c : contentStrings) {
//                                    content[n++] = Integer.parseInt(c);
//                                }
//                                subcontentCells[ii][jj] = new Cell(content);
//                            }
//                        }
//                        cells[i][j] = new SubCell(subcontentCells);
                    } else {
                        List<String> contentStrings = Arrays.asList(line[i].split("/"));
                        int[] content = new int[3];
                        int n = 0;
                        for (String c : contentStrings) {
                            content[n++] = Integer.parseInt(c);
                        }
                        cells[i][j] = new Cell(content);
                    }
                }
            }
            //SubCell sub = (SubCell)cells[5][3];
            maps.put(name, new Map(cells, (int) width, (int) height));
        }
    }

    public static Map getMap(String mapName) {
        return maps.get(mapName);
    }
}
