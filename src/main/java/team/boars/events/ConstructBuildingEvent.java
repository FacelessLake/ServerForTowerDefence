package team.boars.events;

import com.badlogic.gdx.math.MathUtils;
import com.google.gson.JsonObject;
import team.boars.gameactor.Building;
import team.boars.level.Tile;

import static team.boars.server.Main.eventQueue;
import static team.boars.server.Main.messageQueue;

public class ConstructBuildingEvent implements StateEvent {
    int buildingID, tileX, tileY, refID;

    public ConstructBuildingEvent(int buildingID, int tileX, int tileY, int refID) {
        this.buildingID = buildingID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.refID = refID;
    }

    public ConstructBuildingEvent(int buildingID, int tileX, int tileY) {
        this(buildingID, tileX, tileY, -1);
    }

    @Override
    public void execute(StateHolder state) {
        int cost = state.getCreator().getBuildingConfig(buildingID).cost;

        if (cost > state.getCurrency()) return;

        Building newBuilding = state.getCreator().getNewBuilding(buildingID);
        Tile targetTile = state.getMap().mapArr[tileX][tileY];
        newBuilding.setPosition(targetTile.x, targetTile.y);
        eventQueue.addStateEvent(new AlterCurrencyEvent(-cost));

        if (refID == -1) {
            int refID = MathUtils.random(10000);
            while (state.getEnemies().containsKey(refID)) {
                refID = MathUtils.random(10000);
            }
            newBuilding.setRefID(refID);
            state.getBuildings().put(refID, newBuilding);
        } else {
            newBuilding.setRefID(refID);
            state.getBuildings().put(refID, newBuilding);
        }

        JsonObject json = new JsonObject();
        json.addProperty("cmd", "constructBuilding");
        json.addProperty("refID", refID);
        json.addProperty("id", buildingID);
        json.addProperty("gridX", tileX);
        json.addProperty("gridY", tileY);
        try {
            messageQueue.put(json);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
