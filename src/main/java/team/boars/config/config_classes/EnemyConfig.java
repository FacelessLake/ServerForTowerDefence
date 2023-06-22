package team.boars.config.config_classes;

import team.boars.gameactor.action.ActionType;
import team.boars.gameactor.priority.Priority;

import java.util.Map;

public class EnemyConfig {
    public int id;
    public int maxHealth;
    public int reward;
    public Priority priority;
    public float speed;
    public String name;
    public float actionRate;
    public float actionRange;
    public ActionType actionType;
    public Map<String, Float> actionParams;
}
