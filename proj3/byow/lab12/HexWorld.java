package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 160;
    private static final int HEIGHT = 80;
    private static final int HEXNUMBER = 19;
    private TETile[] tiles = {Tileset.WALL, Tileset.FLOWER, Tileset.FLOOR, Tileset.GRASS, Tileset.SAND};

    public static void main(String[] args) {
        HexWorld hexWorld = new HexWorld();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        hexWorld.tessellationHexagons(world, 3, 50, 50);
        ter.renderFrame(world);
    }

    private void tessellationHexagons(TETile[][] world, int size, int startX, int startY) {
        int[][] directions = {
                {2 * size - 1, size}, // 右上
                {2 * size - 1, -size}, // 右下
                {0, -2 * size} // 下方
        };
        Random random = new Random();
        int x = startX;
        int y = startY;
        for (int i = 0; i < HEXNUMBER; i++) {
            TETile tile = tiles[random.nextInt(tiles.length)];
            addHexagon(world, size, x, y, tile);
            int[] direction = directions[random.nextInt(directions.length)];
            x += direction[0];
            y += direction[1];
            if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                x = startX;
                y = startY;
                i--;
            }
        }
    }

    private void addHexagon(TETile[][] world, int size, int x, int y, TETile t) {
        if (size < 2) {
            throw new IllegalArgumentException("Hexagon must be at least size 2.");
        }
        int height = 2 * size;
        for (int i = 1; i <= height; i++) {
            int rowWidth = computeRowWidth(size, i);
            int startX = computeStartX(size, i, x);
            if (startX < 0 || startX + rowWidth > WIDTH || y + i - 1 >= HEIGHT) {
                continue;
            }
            addLine(world, rowWidth, startX, y + i - 1, t);
        }
    }

    private void addLine(TETile[][] world, int size, int startX, int y, TETile t) {
        for (int i = 0; i < size; i++) {
            if (startX + i >= 0 && startX + i < WIDTH && y >= 0 && y < HEIGHT) {
                world[startX + i][y] = t;
            }
        }
    }

    private int computeRowWidth(int size, int row) {
        if (row <= size) {
            return size + 2 * (row - 1);
        } else {
            return size * 3 - 2 * (row - size);
        }
    }

    private int computeStartX(int size, int row, int x) {
        if (row <= size) {
            return x - row + 1;
        } else {
            return x - 2 * size + row;
        }
    }
}