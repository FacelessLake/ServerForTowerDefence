package team.boars.config.config_classes;

import team.boars.gameactor.action.ActionType;
import team.boars.gameactor.priority.Priority;

import java.util.List;
import java.util.Map;

public class BuildingConfig {
    public int id;
    public int maxHealth;
    public Priority priority;
    public int cost;
    public String name;
    public String spriteName;
    public int demolitionCurrency;
    public List<BuildingUpgradeConfig> upgrades;
    public float actionRate;
    public ActionType actionType;
    public float actionRange;
    public Map<String, Float> actionParams;
}
