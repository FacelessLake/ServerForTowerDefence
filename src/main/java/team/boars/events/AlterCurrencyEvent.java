package team.boars.events;

import com.google.gson.JsonObject;

import static team.boars.server.Main.messageQueue;

public class AlterCurrencyEvent implements StateEvent {
    private final int value;

    public AlterCurrencyEvent(int value) {
        this.value = value;
    }

    @Override
    public void execute(StateHolder state) {
        if (state.getCurrency() + value < 0) return;
        state.addCurrency(value);

        JsonObject json = new JsonObject();
        json.addProperty("cmd", "moneyChanged");
        json.addProperty("currency", state.getCurrency());
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
