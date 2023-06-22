package team.boars.config;

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
    public TileType[][] tileMap;
    public Vector2 baseTileCoords;
    public Vector2 spawnerCoords;
}
