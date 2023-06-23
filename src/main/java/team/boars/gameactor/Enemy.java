package team.boars.gameactor;

import com.badlogic.gdx.math.Vector2;
import team.boars.gameactor.action.Action;
import team.boars.config.config_classes.EnemyConfig;
import team.boars.gameactor.priority.Priority;

public class Enemy implements GameActor {
    public final static int NODE_SNAP_DISTANCE = 5;
    private final int id;
    private int health;
    private final int maxHealth;
    private final Priority priority;
    private final Vector2 position;
    private final String name;
    private final Action action;
    private final float speed;
    private boolean isActive;
    private float actionTimer;
    private final ActorType actorType;
    private int refID;
    private GameActor target;
    private Vector2 moveTarget;
    private float frozenDuration;

    public Enemy(EnemyConfig config, Action action, Vector2 position) {
        this.id = config.id;
        this.maxHealth = config.maxHealth;
        this.health = this.maxHealth;
        this.priority = config.priority;
        this.name = config.name;
        this.speed = config.speed;
        this.position = new Vector2(position);
        this.action = action;
        isActive = true;
        actionTimer = action.getRate();
        target = null;
        actorType = ActorType.Enemy;
        frozenDuration = 0;
    }

    public Enemy(EnemyConfig config, Action action) {
        this(config, action, Vector2.Zero);
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public int applyDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
        return health;
    }

    @Override
    public void becomeFrozen(float duration) {
        frozenDuration = duration;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getHealthMax() {
        return maxHealth;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void act(float delta) {
        if (frozenDuration == 0) {
            move(delta);

            if (action.getRate() < 0) return;
            actionTimer -= delta;
            if (actionTimer <= 0) {
                if (action.call(this, delta, target))
                    actionTimer = action.getRate();
            }
        }
        else {
            frozenDuration -= delta;
            if (frozenDuration<0){
                frozenDuration = 0;
            }
        }
    }

    @Override
    public void kill() {
        isActive = false;
    }

    @Override
    public GameActor getTarget() {
        return target;
    }

    @Override
    public void setTarget(GameActor target) {
        this.target = target;
    }

    private void move(float delta) {
        if (target != null) {
            if (target.getPosition().dst(position) <= action.getRange()) {//don't move if target is withing range.
                return;
            }
        }

        if (moveTarget == null) return;
        if (moveTarget.dst(position) > NODE_SNAP_DISTANCE) { //move towards target node.
            Vector2 direction = moveTarget.cpy().sub(position);
            position.add(direction.nor().scl(speed).scl(delta));
        } else { //snap to target node if close enough.
            position.set(moveTarget);
        }
    }

    public void setMoveTarget(Vector2 moveTarget) {
        this.moveTarget = moveTarget;
    }

    @Override
    public ActorType getType() {
        return actorType;
    }

    @Override
    public void setRefID(int refID) {
        this.refID = refID;
    }

    @Override
    public int getRefID() {
        return refID;
    }

    @Override
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }
}
