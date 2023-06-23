package team.boars.events;

import com.google.gson.JsonObject;
import team.boars.gameactor.GameActor;

import static team.boars.server.Main.messageQueue;

public class FreezeActorEvent implements StateEvent{
    private final float duration;
    private final int refID;
    private final boolean targetsEnemy;

    public FreezeActorEvent(float duration, int refID, boolean targetsEnemy) {
        this.duration = duration;
        this.refID = refID;
        this.targetsEnemy = targetsEnemy;
    }

    @Override
    public void execute(StateHolder state) {
        GameActor target;
        if (targetsEnemy) target = state.getEnemies().get(refID);
        else target = state.getBuildings().get(refID);

        if (target != null)
            target.becomeFrozen(duration);

        JsonObject json = new JsonObject();
        json.addProperty("cmd", "freezeActor");
        json.addProperty("refID", refID);
        json.addProperty("isEnemy", !targetsEnemy);
        json.addProperty("duration", duration);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
