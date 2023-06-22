package team.boars.events;

import com.google.gson.JsonObject;
import team.boars.gameactor.GameActor;

import static team.boars.server.Main.messageQueue;

public class DamageActorEvent implements StateEvent {
    private final int damage, refID;
    private final boolean targetsEnemy;

    public DamageActorEvent(int damage, int refID, boolean targetsEnemy) {
        this.damage = damage;
        this.refID = refID;
        this.targetsEnemy = targetsEnemy;
    }

    @Override
    public void execute(StateHolder state) {
        GameActor target;
        if (targetsEnemy) target = state.getEnemies().get(refID);
        else target = state.getBuildings().get(refID);

        if (target != null)
            target.applyDamage(damage);

        JsonObject json = new JsonObject();
        json.addProperty("cmd", "damageActor");
        json.addProperty("refID", refID);
        json.addProperty("isEnemy", targetsEnemy);
        json.addProperty("damage", damage);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
