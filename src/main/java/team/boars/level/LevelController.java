package team.boars.level;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import team.boars.config.Creator;
import team.boars.config.LevelConfig;
import team.boars.events.ActorDeathEvent;
import team.boars.events.ConstructBuildingEvent;
import team.boars.events.StateHolder;
import team.boars.gameactor.Building;
import team.boars.gameactor.Enemy;
import team.boars.gameactor.GameActor;
import team.boars.sprite.Projectile;

import java.util.ArrayList;
import java.util.List;

import static team.boars.server.Main.*;

public class LevelController {
    private static final int PATHFINDING_UPDATE_RATE = 0; //optional parameter for optimization.
    private final StateHolder levelState;
    private WaveGenerator waveGenerator;
    private float pathfindingTimer;
    private List<Projectile> projectiles = new ArrayList<>();
    private List<Projectile> deadProjectiles = new ArrayList<>();
    private float actorStateTimer = 0.3f;

    public LevelController(Creator creator, int levelID) {
        LevelConfig levelConfig = creator.getLevelConfig(levelID);
        levelState = new LevelState(creator, levelConfig);
        Vector2 basePosition = levelConfig.baseTileCoords;
        eventQueue.addStateEvent(new ConstructBuildingEvent(0, (int) basePosition.x, (int) basePosition.y, 0));
        pathfindingTimer = 0;
        waveGenerator = new WaveGenerator(levelConfig);
        waveGenerator.start();
    }

    public void update(float delta) {
        waveGenerator.update(delta);

        for (int key : levelState.getBuildings().keySet()) {
            updateActor(levelState.getBuildings().get(key), key, delta, false);
        }
        for (int key : levelState.getEnemies().keySet()) {
            updateActor(levelState.getEnemies().get(key), key, delta, true);
        }
        for (Projectile projectile : projectiles) {
            projectile.act(delta);
            if (!projectile.active) {
                deadProjectiles.add(projectile);
            }
        }
        for (Projectile deadProjectile : deadProjectiles) {
            projectiles.remove(deadProjectile);
        }
        if (pathfindingTimer <= 0 && PATHFINDING_UPDATE_RATE != 0) pathfindingTimer = PATHFINDING_UPDATE_RATE;
        else pathfindingTimer -= delta;
        actorStateTimer -= delta;
        if (actorStateTimer <= 0){
            sendActorStates();
            actorStateTimer = 0.3f;
        }
    }

    private void sendActorStates(){
        JsonObject json = new JsonObject();
        json.addProperty("cmd", "actorStates");
        JsonArray jsonEnemiesArray = new JsonArray();
        for (GameActor enemy : levelState.getEnemies().values()) {
            JsonObject enemyJson = new JsonObject();
            enemyJson.addProperty("refID", enemy.getRefID());
            enemyJson.addProperty("id", enemy.getID());
            enemyJson.addProperty("health", enemy.getHealth());
            enemyJson.addProperty("x", enemy.getPosition().x);
            enemyJson.addProperty("y", enemy.getPosition().y);

            jsonEnemiesArray.add(enemyJson);
        }
        json.add("enemies",jsonEnemiesArray);

        JsonArray jsonBuildingsArray = new JsonArray();
        for (GameActor building : levelState.getBuildings().values()) {
            JsonObject buildingJson = new JsonObject();
            buildingJson.addProperty("refID", building.getRefID());
            buildingJson.addProperty("id", building.getID());
            buildingJson.addProperty("health", building.getHealth());
            Tile tile = levelState.getMap().positionToTile(building.getPosition().x, building.getPosition().y);
            buildingJson.addProperty("gridX", tile.gridX);
            buildingJson.addProperty("gridY", tile.gridY);
            buildingJson.addProperty("buildTimeRemaining", ((Building)building).getBuildTimer());
            jsonBuildingsArray.add(buildingJson);
        }
        json.add("buildings", jsonBuildingsArray);
        json.addProperty("timer", waveGenerator.getWaveTimer());
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateActor(GameActor actor, int refID, float delta, boolean isEnemy) {
        if (!actor.isActive()) return;

        if (pathfindingTimer <= 0) {
            if (isEnemy) {
                actor.setTarget(actor.getPriority().chooseTarget(actor.getPosition(), levelState.getBuildings().values()));
                if (actor.getTarget() != null) {
                    Vector2 nextNode = levelState.getMap().getPath(actor.getPosition(), actor.getTarget().getPosition());
                    Enemy e = (Enemy) actor;
                    e.setMoveTarget(nextNode);
                }
            } else {
                actor.setTarget(actor.getPriority().chooseTarget(actor.getPosition(), levelState.getEnemies().values()));
            }
        }
        //actions
        actor.act(delta);
        if (actor.getHealth() <= 0) {
            actor.kill();
            eventQueue.addStateEvent(new ActorDeathEvent(refID, isEnemy));
        }
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public StateHolder getLevelState() {
        return levelState;
    }
}
