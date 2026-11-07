package com.mobius.stream.Game.Event.Game.Entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.GameEvent;

public class EntityHitEvent extends GameEvent implements Pool.Poolable {
    public EntityActor source;
    public EntityActor target;
    public float amount;

    public Vector2 knockback = new Vector2();

    public EntityHitEvent set(EntityActor source, EntityActor target, float amount) {
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
