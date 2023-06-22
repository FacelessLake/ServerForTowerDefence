package team.boars.level;

import team.boars.framework.TileType;

public class Tile {
    public final TileType type;
    public final float x, y;
    public final int gridX, gridY;

    public Tile(TileType type, float x, float y, int gridX, int gridY) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.gridX = gridX;
        this.gridY = gridY;
    }
}
