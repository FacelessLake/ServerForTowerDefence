package team.boars.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import team.boars.config.config_classes.*;
import team.boars.gameactor.priority.DefaultPriority;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deserializer {
    private final Gson gson;
    private final String path;
    private final Map<String, String> fileNames;

    public Deserializer(String path) {
        gson = new GsonBuilder().create();
        this.path = path;

        fileNames = new HashMap<>();
        fileNames.put("Buildings", "BuildingsConfig.json");
        fileNames.put("Enemies", "EnemiesConfig.json");
        fileNames.put("Levels", "LevelsConfig.json");
        fileNames.put("Maps", "MapsConfig.json");
        fileNames.put("TechTree", "TechTreeConfig.json");
    }

    public Map<Integer, BuildingConfig> deserializeBuildings() {
        List<BuildingConfig> buildings;
        Reader reader;
        try {
            reader = new FileReader(path.concat(fileNames.get("Buildings")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Type type = new TypeToken<ArrayList<BuildingConfig>>() {
        }.getType();
        buildings = gson.fromJson(reader, type);
        Map<Integer, BuildingConfig> buildingsMap = new HashMap<>();
        for (int i = 0; i < buildings.size(); i++) {
            BuildingConfig building = buildings.get(i);
            building.priority = new DefaultPriority();
            buildingsMap.put(i, building);
        }
        return buildingsMap;
    }

    public Map<Integer, EnemyConfig> deserializeEnemies() {
        List<EnemyConfig> enemies;
        Reader reader;
        try {
            reader = new FileReader(path.concat(fileNames.get("Enemies")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Type type = new TypeToken<ArrayList<EnemyConfig>>() {
        }.getType();
        enemies = gson.fromJson(reader, type);
        Map<Integer, EnemyConfig> enemiesMap = new HashMap<>();
        for (int i = 0; i < enemies.size(); i++) {
            EnemyConfig enemy = enemies.get(i);
            enemy.priority = new DefaultPriority();
            enemiesMap.put(i, enemy);
        }
        return enemiesMap;
    }

    private Map<Integer, MapConfig> deserializeMaps() {
        List<MapConfig> maps;
        Reader reader;
        try {
            reader = new FileReader(path.concat(fileNames.get("Maps")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Type type = new TypeToken<ArrayList<MapConfig>>() {
        }.getType();
        maps = gson.fromJson(reader, type);
        Map<Integer, MapConfig> mapMap = new HashMap<>();
        for (int i = 0; i < maps.size(); i++) {
            mapMap.put(i, maps.get(i));
        }
        return mapMap;
    }

    public Map<Integer, LevelConfig> deserializeLevels() {
        Map<Integer, MapConfig> maps = this.deserializeMaps();

        List<LevelConfig> levels;
        Reader reader;
        try {
            reader = new FileReader(path.concat(fileNames.get("Levels")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Type type = new TypeToken<ArrayList<LevelConfig>>() {
        }.getType();
        levels = gson.fromJson(reader, type);

        Map<Integer, LevelConfig> levelsMap = new HashMap<>();
        for (int i = 0; i < levels.size(); i++) {
            LevelConfig level = levels.get(i);
            level.tileMap = maps.get(level.mapID).field;
            level.baseTileCoords = maps.get(level.mapID).baseCoordinates;
            level.spawnerCoords = maps.get(level.mapID).spawnerCoordinates;
            levelsMap.put(i, level);
        }
        return levelsMap;
    }

    public TechTreeConfig deserializeTechTree() {
        Reader reader;
        try {
            reader = new FileReader(path.concat(fileNames.get("TechTree")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Type type = new TypeToken<TechTreeConfig>() {
        }.getType();
        return gson.fromJson(reader, type);
    }
}
