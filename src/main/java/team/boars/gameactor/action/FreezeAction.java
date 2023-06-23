package team.boars.gameactor.action;

import com.google.gson.JsonObject;
import team.boars.events.FreezeActorEvent;
import team.boars.gameactor.ActorType;
import team.boars.gameactor.GameActor;

import java.util.Map;

import static team.boars.server.Main.*;

public class FreezeAction extends DoNothingAction {
    private float duration;
    //private float range;
    public final static String[] argList = new String[]{"duration"};

    public FreezeAction(float rate, float range, Map<String, Float> params) {
        super(rate, range, params);
        duration = Math.round(params.get(argList[0]));
    }

    @Override
    public boolean call(GameActor caller, float delta, GameActor target) {
        if (target == null) return false;
        boolean targetsEnemy = (target.getType() == ActorType.Enemy);

        if (target.getPosition().dst(caller.getPosition()) <= getRange()) {
            eventQueue.addStateEvent(new FreezeActorEvent(duration, target.getRefID(), targetsEnemy));
            JsonObject json = new JsonObject();
            json.addProperty("cmd", "freeze");
            json.addProperty("x", target.getPosition().x);
            json.addProperty("y", target.getPosition().y);
            json.addProperty("duration",duration);
            try {
                messageQueue.put(json);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
