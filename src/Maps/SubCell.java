//package Maps;
//
//import Textures.Textures;
//import org.jsfml.graphics.Color;
//import org.jsfml.graphics.RenderWindow;
//import org.jsfml.graphics.Sprite;
//
///**
// * Created by Thomas VENNER on 08/09/2016.
// */
//public class SubCell extends Cell {
//    private NormalCell[][] cells;
//    public NormalCell[][] getCells() {
//        return cells;
//    }
//
//    public SubCell(NormalCell[][] cells) {
//        this.cells = new NormalCell[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                this.cells[i][j] = cells[i][j].clone();
//            }
//        }
//    }
//
//    public boolean isWalkable(int i, int j) {
//        return cells[i][j].isWalkable();
//    }
//
//    @Override
//    public void drawFirstLayer(RenderWindow window, int x, int y) {
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Sprite sprite = new Sprite();
//                sprite.setPosition(x + i * (Map.SIZE / 8), y + j * (Map.SIZE / 8));
//                sprite.setScale(Map.SIZE / 8, Map.SIZE / 8);
//                switch (this.cells[i][j].getContent()[0]) {
//                    case 1: // Wall
//                        sprite.setTexture(Textures.getPixel());
//                        sprite.setColor(Color.BLACK);
//                        break;
//                    default:
//                        break;
//                }
//                window.draw(sprite);
//            }
//        }
//    }
//}
