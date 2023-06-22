package team.boars.config;

import team.boars.config.config_classes.BuildingConfig;
import team.boars.config.config_classes.EnemyConfig;
import team.boars.config.config_classes.LevelConfig;
import team.boars.gameactor.action.*;
import team.boars.gameactor.Building;
import team.boars.gameactor.Enemy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Creator {
    private final Deserializer deserializer;

    private final Map<Integer, BuildingConfig> buildingConfigMap;
    private final Map<Integer, EnemyConfig> enemyConfigMap;
    private final Map<Integer, LevelConfig> levelConfigMap;
    private final Map<ActionType, Class<?>> actionClasses;

    public Creator() {
        deserializer = new Deserializer("assets/configs/");
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
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LevelConfig getLevelConfig(int ID) {
        return levelConfigMap.get(ID);
    }

    private void initActions() {
        actionClasses.put(ActionType.DoNothing, DoNothingAction.class);
        actionClasses.put(ActionType.BasicAttack, BasicAttackAction.class);
        actionClasses.put(ActionType.GenerateCurrency, GenerateCurrencyAction.class);
    }

    private void initBuildings() {
        buildingConfigMap.putAll(deserializer.deserializeBuildings());
    }

    private void initEnemies() {
        enemyConfigMap.putAll(deserializer.deserializeEnemies());
    }

    private void initLevels() {
        levelConfigMap.putAll(deserializer.deserializeLevels());
    }

}
