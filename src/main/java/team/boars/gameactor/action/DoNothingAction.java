package team.boars.gameactor.action;

import team.boars.gameactor.GameActor;

import java.util.Map;

public class DoNothingAction implements Action {
    private final float rate;
    private final float range;
    private final static String[] argList = new String[]{};

    public DoNothingAction(float rate, float range, Map<String, Float> params) {
        this.rate = rate;
        this.range = range;
    }

    @Override
    public boolean call(GameActor caller, float delta, GameActor target) {
        return false;
    }

    @Override
    public float getRate() {
        return rate;
    }

    @Override
    public float getRange() {
        return range;
    }
}
