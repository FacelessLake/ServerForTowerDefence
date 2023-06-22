package team.boars.events;

import com.google.gson.JsonObject;

import static team.boars.server.Main.*;

public class LevelEndEvent implements StateEvent {
    private final boolean victory;
    private final int reward;
    public LevelEndEvent(boolean victory, int reward) {
        this.victory = victory;
        this.reward = reward;
    }
    @Override
    public void execute(StateHolder state) {
        JsonObject json = new JsonObject();
        json.addProperty("cmd", "endGame");
        json.addProperty("victory", victory);
        json.addProperty("reward", reward);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        globalRestart();
    }
}
