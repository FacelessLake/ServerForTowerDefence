package team.boars.events;

import com.badlogic.gdx.math.MathUtils;
import com.google.gson.JsonObject;
import team.boars.gameactor.Enemy;
import static team.boars.server.Main.messageQueue;

public class SpawnEnemyEvent implements StateEvent{
    int enemyID, gridX, gridY;

    public SpawnEnemyEvent(int enemyID, int gridX, int gridY) {
        this.enemyID = enemyID;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    @Override
    public void execute(StateHolder state) {
        Enemy newEnemy = state.getCreator().getNewEnemy(enemyID);
        newEnemy.setPosition(state.getMap().mapArr[gridX][gridY].x, state.getMap().mapArr[gridX][gridY].y);
        newEnemy.setMoveTarget(newEnemy.getPosition());
        int refID = MathUtils.random(10000);
        while (state.getEnemies().containsKey(refID)) {
            refID = MathUtils.random(10000);
        }

        JsonObject json = new JsonObject();
        json.addProperty("cmd", "spawnEnemy");
        json.addProperty("refID", refID);
        json.addProperty("id", enemyID);
        json.addProperty("gridX", gridX);
        json.addProperty("gridY", gridY);
        newEnemy.setRefID(refID);
        state.getEnemies().put(refID, newEnemy);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
