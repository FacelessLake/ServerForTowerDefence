package team.boars.gameactor.action;

import com.google.gson.JsonObject;
import team.boars.events.DamageActorEvent;
import team.boars.framework.LevelView;
import team.boars.gameactor.ActorType;
import team.boars.gameactor.GameActor;
import team.boars.sprite.Projectile;

import java.util.Map;

import static team.boars.server.Main.*;

public class BasicAttackAction extends DoNothingAction {
    private final float range;
    private final int damage;
    public final static String[] argList = new String[]{"damage"};

    public BasicAttackAction(float rate, float range, Map<String, Float> params) {
        super(rate, range, params);
        this.range = range;
        damage = Math.round(params.get(argList[0]));
    }

    @Override
    public boolean call(GameActor caller, float delta, GameActor target) {
        if (target == null) return false;
        boolean targetsEnemy = (target.getType() == ActorType.Enemy);
        JsonObject json = new JsonObject();

        if (target.getPosition().dst(caller.getPosition()) <= range) {
            if (range <= LevelView.TilE_SIZE / 2) {
                //melee
                eventQueue.addStateEvent(new DamageActorEvent(target.getRefID(), damage, targetsEnemy));
                json.addProperty("cmd", "meleeAttack");
                json.addProperty("attackerRefID", caller.getRefID());
                json.addProperty("targetRefID", target.getRefID());
                json.addProperty("isAttackerEnemy", !targetsEnemy);
                json.addProperty("damage", damage);
            } else {
                //ranged
                json.addProperty("cmd", "rangedAttack");
                json.addProperty("x", caller.getPosition().x);
                json.addProperty("y", caller.getPosition().y);
                json.addProperty("targetRefID", target.getRefID());
                json.addProperty("isAttackerEnemy", !targetsEnemy);
                json.addProperty("damage", damage);

                Projectile projectile = new Projectile(
                        caller.getPosition().x,
                        caller.getPosition().y,
                        target,
                        damage,
                        targetsEnemy);
                controller.addProjectile(projectile);
            }
            try {
                messageQueue.put(json);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }
}
