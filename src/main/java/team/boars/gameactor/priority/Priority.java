package team.boars.gameactor.priority;

import com.badlogic.gdx.math.Vector2;
import team.boars.gameactor.GameActor;

import java.util.Collection;

public interface Priority {
    GameActor chooseTarget(Vector2 position, Collection<GameActor> actors);
}
