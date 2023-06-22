package team.boars.config;

import com.badlogic.gdx.math.Vector2;
import team.boars.gameactor.action.*;
import team.boars.framework.TileType;
import team.boars.gameactor.Building;
import team.boars.gameactor.Enemy;
import team.boars.gameactor.priority.DefaultPriority;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Creator {
    private final Map<Integer, BuildingConfig> buildingConfigMap;
    private final Map<Integer, EnemyConfig> enemyConfigMap;
    private final Map<Integer, LevelConfig> levelConfigMap;
    private final Map<ActionType, Class<?>> actionClasses;

    public Creator() {
        buildingConfigMap = new HashMap<>();
        enemyConfigMap = new HashMap<>();
        levelConfigMap = new HashMap<>();
        actionClasses = new HashMap<>();
        initActions();
        initBuildings();
        initEnemies();
        initLevels();
    }

    public Building getNewBuilding(int ID) {
        BuildingConfig config = buildingConfigMap.get(ID);
        Action action = getAction(config.actionType, config.actionRate, config.actionRange, config.actionParams);
        return new Building(config, action);
    }

    public Enemy getNewEnemy(int ID) {
        EnemyConfig config = enemyConfigMap.get(ID);
        Action action = getAction(config.actionType, config.actionRate, config.actionRange, config.actionParams);
        return new Enemy(config, action);
    }

    public EnemyConfig getEnemyConfig(int ID) {
        return enemyConfigMap.get(ID);
    }

    public BuildingConfig getBuildingConfig(int ID) {
        return buildingConfigMap.get(ID);
    }

    public Action getAction(ActionType actionType, float rate, float range, Map<String, Float> params) {
        Class<?> actionClass = actionClasses.get(actionType);
        Constructor<?> ctor;
        try {
            ctor = actionClass.getConstructor(float.class, float.class, Map.class);
            return (Action) ctor.newInstance(rate, range, params);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LevelConfig getLevelConfig(int ID) {
        return levelConfigMap.get(ID);
    }

    public Map<Integer, BuildingConfig> getBuildingMap() {
        return buildingConfigMap;
    }

    public Map<Integer, EnemyConfig> getEnemyConfigMap() {
        return enemyConfigMap;
    }

    public Map<Integer, LevelConfig> getLevelConfigMap() {
        return levelConfigMap;
    }

    private void initActions() {
        actionClasses.put(ActionType.DoNothing, DoNothingAction.class);
        actionClasses.put(ActionType.BasicAttack, BasicAttackAction.class);
        actionClasses.put(ActionType.GenerateCurrency, GenerateCurrencyAction.class);
    }

    private void initBuildings() {
        //shoud have parsing here instead of manual creation.
        BuildingConfig base = new BuildingConfig();
        base.actionParams = new HashMap<>();
        base.actionRange = 0;
        base.actionRate = -1;
        base.actionType = ActionType.DoNothing;
        base.id = 0;
        base.cost = 0;
        base.demolitionCurrency = 0;
        base.maxHealth = 100;
        base.name = "Base";
        base.priority = new DefaultPriority();
        base.SpriteName = "base.png";

        BuildingConfig basicTower = new BuildingConfig();
        basicTower.actionParams = new HashMap<>();
        basicTower.actionParams.put("damage", 5f);
        basicTower.actionRange = 120;
        basicTower.actionRate = 1;
        basicTower.actionType = ActionType.BasicAttack;
        basicTower.id = 1;
        basicTower.cost = 50;
        basicTower.demolitionCurrency = 10;
        basicTower.maxHealth = 30;
        basicTower.name = "Guard Tower";
        basicTower.priority = new DefaultPriority();
        basicTower.SpriteName = "tower.png";

        BuildingConfig mine = new BuildingConfig();
        mine.actionParams = new HashMap<>();
        mine.actionParams.put("value", 5f);
        mine.actionRange = 0;
        mine.actionRate = 1;
        mine.actionType = ActionType.GenerateCurrency;
        mine.id = 2;
        mine.cost = 10;
        mine.demolitionCurrency = 10;
        mine.maxHealth = 30;
        mine.name = "Gold Mine";
        mine.priority = new DefaultPriority();
        mine.SpriteName = "mine.png";

        buildingConfigMap.put(base.id, base);
        buildingConfigMap.put(basicTower.id, basicTower);
        buildingConfigMap.put(mine.id, mine);
    }

    private void initEnemies() {
        EnemyConfig meleeEnemy = new EnemyConfig();
        meleeEnemy.actionParams = new HashMap<>();
        meleeEnemy.actionParams.put("damage", 5f);
        meleeEnemy.actionRange = 45;
        meleeEnemy.actionRate = 1.5f;
        meleeEnemy.id = 1;
        meleeEnemy.actionType = ActionType.BasicAttack;
        meleeEnemy.maxHealth = 15;
        meleeEnemy.name = "Boar Warrior";
        meleeEnemy.priority = new DefaultPriority();
        meleeEnemy.reward = 5;
        meleeEnemy.speed = 100;
        meleeEnemy.SpriteName = "boar.png";

        EnemyConfig rangedEnemy = new EnemyConfig();
        rangedEnemy.actionParams = new HashMap<>();
        rangedEnemy.actionParams.put("damage", 3f);
        rangedEnemy.actionRange = 100;
        rangedEnemy.actionRate = 1.5f;
        rangedEnemy.id = 2;
        rangedEnemy.actionType = ActionType.BasicAttack;
        rangedEnemy.maxHealth = 10;
        rangedEnemy.name = "Boar Ranger";
        rangedEnemy.priority = new DefaultPriority();
        rangedEnemy.reward = 5;
        rangedEnemy.speed = 150;
        rangedEnemy.SpriteName = "boarRanger.png";

        enemyConfigMap.put(meleeEnemy.id, meleeEnemy);
        enemyConfigMap.put(rangedEnemy.id, rangedEnemy);
    }

    private void initLevels() {
        LevelConfig level = new LevelConfig();
        level.id = 0;
        level.backgroundTextureName = "background.png";
        level.roadTextureName = "road.png";
        level.plotTextureName = "plot.png";
        level.startingCurrency = 10;
        level.tileMap = new TileType[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                level.tileMap[i][j] = TileType.Background;
            }
        }
        level.tileMap[0][2] = TileType.Road;
        level.tileMap[1][2] = TileType.Road;
        level.tileMap[2][2] = TileType.Road;
        level.tileMap[3][2] = TileType.Road;
        level.tileMap[3][3] = TileType.Road;
        level.tileMap[3][4] = TileType.Road;
        level.tileMap[3][5] = TileType.Road;
        level.tileMap[4][5] = TileType.Road;
        level.tileMap[5][5] = TileType.Road;
        level.tileMap[5][4] = TileType.Road;
        level.tileMap[6][4] = TileType.Road;
        level.tileMap[7][4] = TileType.Road;
        level.tileMap[8][4] = TileType.Plot;
        level.tileMap[2][1] = TileType.Plot;
        level.tileMap[5][3] = TileType.Plot;
        level.tileMap[5][6] = TileType.Plot;
        level.spawnerCoords = new Vector2(0, 2);
        level.baseTileCoords = new Vector2(8, 4);
        level.waves = new LinkedList<>();
        WaveConfig wave = new WaveConfig();
        wave.enemyCount = 5;
        wave.waveDelay = 20;
        wave.enemyInterval = 4;
        wave.enemyTypes = new LinkedList<>();
        wave.enemyTypes.add(1);
        wave.enemyTypes.add(2);
        level.waves.add(wave);

        levelConfigMap.put(level.id, level);
    }
}
