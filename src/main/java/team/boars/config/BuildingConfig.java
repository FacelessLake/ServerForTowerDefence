package team.boars.config;

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
    public String SpriteName;
    public int demolitionCurrency;
    public List<Integer> upgradeIDs;
    public float actionRate;
    public ActionType actionType;
    public float actionRange;
    public Map<String, Float> actionParams;
}
