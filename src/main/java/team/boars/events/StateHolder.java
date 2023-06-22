package team.boars.events;

import team.boars.config.Creator;
import team.boars.gameactor.GameActor;
import team.boars.level.LevelMapState;

import java.util.Map;

public interface StateHolder {
    Map<Integer, GameActor> getBuildings();

    Map<Integer, GameActor> getEnemies();

    LevelMapState getMap();

    int getCurrency();

    void addCurrency(int currency);

    Creator getCreator();

    void markLastEnemySpawn();

    boolean isLastEnemySpawned();
}
