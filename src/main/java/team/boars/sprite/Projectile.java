package team.boars.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import team.boars.events.DamageActorEvent;
import team.boars.framework.LevelView;
import team.boars.gameactor.GameActor;

import static team.boars.server.Main.eventQueue;

public class Projectile {
    public boolean active;
    private final int damage;
    private float x;
    private float y;
    private final GameActor target;
    private final double width;
    private final double height;
    private final int targetRefID;
    private final boolean targetsEnemy;

    public Projectile(float x, float y, GameActor target, int damage, boolean targetsEnemy) {
        active = true;
        this.damage = damage;
        this.x = x;
        this.y = y;
        this.target = target;
        if (targetsEnemy) {
            width = LevelView.TilE_SIZE * 0.8;
            height = LevelView.TilE_SIZE * 0.8;
        } else {
            width = LevelView.TilE_SIZE * 0.5;
            height = LevelView.TilE_SIZE * 0.5;
        }
        this.targetRefID = target.getRefID();
        this.targetsEnemy = targetsEnemy;
    }

    public void act(float delta) {
        if (active){
            if (collidesWithTarget()) {
                eventQueue.addStateEvent(new DamageActorEvent(damage, targetRefID, targetsEnemy));
                active = false;
            }
            Vector2 targetPos = new Vector2(target.getPosition().x, target.getPosition().y);
            Vector2 projPos = new Vector2(x, y);
            Vector2 direction = targetPos.sub(projPos);
            projPos.add(direction.nor().scl(1000).scl(delta));
            x = projPos.x;
            y = projPos.y;
        }
    }

    private boolean collidesWithTarget() {
        boolean a = (x >= target.getPosition().x - width / 2);
        boolean c = (y >= target.getPosition().y - height / 2);
        boolean b = (x <= target.getPosition().x + width / 2);
        boolean d = (y <= target.getPosition().y + height / 2);
        return a && b && c && d;
    }
}
