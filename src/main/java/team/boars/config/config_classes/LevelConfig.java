package team.boars.config.config_classes;

import com.badlogic.gdx.math.Vector2;
import team.boars.framework.TileType;

import java.util.List;

public class LevelConfig {
    public int id;
    public String backgroundTextureName;
    public String plotTextureName;
    public String roadTextureName;
    public List<WaveConfig> waves;
    public int startingCurrency;
    public int mapID;
    public TileType[][] tileMap;
    public Vector2 baseTileCoords;
    public Vector2 spawnerCoords;
    public int reward;
}
