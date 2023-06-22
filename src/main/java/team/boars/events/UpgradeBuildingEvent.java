package team.boars.events;

import team.boars.config.config_classes.BuildingConfig;
import team.boars.config.config_classes.BuildingUpgradeConfig;
import team.boars.gameactor.Building;

import static team.boars.server.Main.*;

public class UpgradeBuildingEvent implements StateEvent {
    private final int refID, upgradeID;

    public UpgradeBuildingEvent(int refID, int upgradeID) {
        this.refID = refID;
        this.upgradeID = upgradeID;
    }

    @Override
    public void execute(StateHolder state) {
        Building building = (Building) state.getBuildings().get(refID);
        if (building == null) return;
        BuildingConfig config = state.getCreator().getBuildingConfig(building.getID());
        BuildingUpgradeConfig upgrade = config.upgrades.get(upgradeID);
        if (upgrade.cost > state.getCurrency()) return;

        eventQueue.addStateEvent(new AlterCurrencyEvent(-upgrade.cost));
        building.applyUpgrade(upgrade, upgradeID);
    }
}
