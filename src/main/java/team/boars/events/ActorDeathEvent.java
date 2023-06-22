package team.boars.events;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import team.boars.config.EnemyConfig;
import team.boars.level.Tile;
import static team.boars.server.Main.*;

public class ActorDeathEvent implements StateEvent {
    private final int refID;
    private final boolean isEnemy;

    public ActorDeathEvent(int refID, boolean isEnemy) {
        this.refID = refID;
        this.isEnemy = isEnemy;
    }

    @Override
    public void execute(StateHolder state) {
        if (isEnemy) {
            EnemyConfig config = state.getCreator().getEnemyConfig(state.getEnemies().get(refID).getID());
            eventQueue.addStateEvent(new AlterCurrencyEvent(config.reward));
            state.getEnemies().remove(refID);
        } else {
            Vector2 pos = state.getBuildings().get(refID).getPosition();
            Tile tile = state.getMap().positionToTile(pos.x, pos.y);
            state.getBuildings().remove(refID);
        }
        JsonObject json = new JsonObject();
        json.addProperty("cmd", "actorDeath");
        json.addProperty("refID", refID);
        json.addProperty("isEnemy", isEnemy);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
