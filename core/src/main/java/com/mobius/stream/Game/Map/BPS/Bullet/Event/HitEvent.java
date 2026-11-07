package com.mobius.stream.Game.Map.BPS.Bullet.Event;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class HitEvent implements Pool.Poolable {
    public Entity source;
    public Entity target;
    public float amount;

    public Vector2 knockback = new Vector2();

    public HitEvent set(Entity source, Entity target, float amount) {
        this.source = source;
        this.target = target;
        this.amount = amount;
        return this;
    }


    @Override
    public void reset() {
        source = target = null;
        amount = 0f;
        knockback.setZero();
    }
}
