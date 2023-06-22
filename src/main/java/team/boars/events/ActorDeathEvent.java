package team.boars.events;

import com.google.gson.JsonObject;
import team.boars.config.config_classes.EnemyConfig;

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
            int id = state.getBuildings().get(refID).getID();
            state.getBuildings().remove(refID);
            if (id == 0) {
                eventQueue.addStateEvent(new LevelEndEvent(false, 0));
            }
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
