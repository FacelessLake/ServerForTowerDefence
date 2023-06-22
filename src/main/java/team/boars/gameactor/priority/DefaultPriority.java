package team.boars.gameactor.priority;

import com.badlogic.gdx.math.Vector2;
import team.boars.gameactor.GameActor;

import java.util.Collection;

public class DefaultPriority implements Priority {
    public DefaultPriority() {
    }

    @Override
    public GameActor chooseTarget(Vector2 position, Collection<GameActor> actors) {
        GameActor target = null;
        //Simply selects closest available target
        float minRange = Float.MAX_VALUE;
        for (GameActor actor : actors) {
            if (position.cpy().sub(actor.getPosition()).len2() <= minRange) {
                minRange = position.cpy().sub(actor.getPosition()).len2();
                target = actor;
            }
        }
        return target;
    }
}
