package team.boars.config.config_classes;

import com.badlogic.gdx.math.Vector2;
import team.boars.framework.TileType;

public class MapConfig {

    public int width;
    public int height;

    public Vector2 baseCoordinates;
    public Vector2 spawnerCoordinates;

    public TileType[][] field;
}
