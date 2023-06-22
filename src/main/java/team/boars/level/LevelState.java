package team.boars.level;

import team.boars.config.Creator;
import team.boars.config.config_classes.LevelConfig;
import team.boars.events.StateHolder;
import team.boars.gameactor.GameActor;

import java.util.HashMap;
import java.util.Map;

import static team.boars.framework.LevelView.*;

public class LevelState implements StateHolder {
    private final Map<Integer, GameActor> activeBuildings;
    private final Map<Integer, GameActor> activeEnemies;
    private final LevelMapState tileMap;
    private int inLevelCurrency;
    private final Creator creator;
    private boolean lastEnemySpawn;

    public final float mapCornerX;
    public final float mapCornerY;

    public LevelState(Creator creator, LevelConfig config) {
        inLevelCurrency = config.startingCurrency;
        activeBuildings = new HashMap<>();
        activeEnemies = new HashMap<>();
        tileMap = new LevelMapState(config.tileMap);
        mapCornerX = (WORLD_SIZE_X - (config.tileMap.length * TilE_SIZE)) / 2;
        mapCornerY = (WORLD_SIZE_Y - (config.tileMap[0].length * TilE_SIZE)) / 2;
        this.creator = creator;
        lastEnemySpawn = false;
    }

    @Override
    public Map<Integer, GameActor> getBuildings() {
        return activeBuildings;
    }

    @Override
    public Map<Integer, GameActor> getEnemies() {
        return activeEnemies;
    }

    @Override
    public LevelMapState getMap() {
        return tileMap;
    }

    @Override
    public int getCurrency() {
        return inLevelCurrency;
    }

    @Override
    public void addCurrency(int currency) {
        inLevelCurrency += currency;
    }

    @Override
    public Creator getCreator() {
        return creator;
    }

    @Override
    public void markLastEnemySpawn() {
        lastEnemySpawn = true;
    }

    @Override
    public boolean isLastEnemySpawned() {
        return lastEnemySpawn;
    }
}
