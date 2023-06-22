package team.boars.gameactor.action;

import team.boars.gameactor.GameActor;

public interface Action {
    boolean call(GameActor caller, float delta, GameActor target);
    float getRate();
    float getRange();
}
