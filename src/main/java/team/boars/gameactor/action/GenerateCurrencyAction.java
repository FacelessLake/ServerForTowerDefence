package team.boars.gameactor.action;

import com.google.gson.JsonObject;
import team.boars.events.AlterCurrencyEvent;
import team.boars.gameactor.GameActor;


import java.util.Map;

import static team.boars.server.Main.eventQueue;
import static team.boars.server.Main.messageQueue;

public class GenerateCurrencyAction extends DoNothingAction {
    private final static String[] argList = new String[]{"value"};
    private int value;

    public GenerateCurrencyAction(float rate, float range, Map<String, Float> params) {
        super(rate, range, params);
        value = Math.round(params.get(argList[0]));
    }

    @Override
    public boolean call(GameActor caller, float delta, GameActor target) {
        eventQueue.addStateEvent(new AlterCurrencyEvent(value));
        JsonObject json = new JsonObject();
        json.addProperty("cmd", "mineCurrency");
        json.addProperty("refID", caller.getRefID());
        json.addProperty("amount", value);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
