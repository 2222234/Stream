package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Event.Game.Entity.EntityHitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.EntityActorComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;

public class SyncAfterSystem extends EntitySystem {
    private final PoolManager signalPool;
    private final Array<HitEvent> hitQueue;

    private final ComponentMapper<EntityActorComponent> eacm = ComponentMapper.getFor(EntityActorComponent.class);

    public SyncAfterSystem(PoolManager signalPool, Array<HitEvent> hitQueue) {
        this.signalPool = signalPool;
        this.hitQueue = hitQueue;
    }

    @Override
    public void update(float delta) {
        for (int i = 0; i < hitQueue.size; i++) {
            HitEvent hitEvent = hitQueue.get(i);
            Entity target = hitEvent.target;
            if (target == null) return;

            EntityActorComponent eac = eacm.get(target);
            if (eac != null) {
                EntityHitEvent entityHitEvent = signalPool.obtain(EntityHitEvent.class);
                if (hitEvent.source != null) {
                    EntityActorComponent eac2 = eacm.get(hitEvent.source);
                    if (eac2 != null) {
                        entityHitEvent.source = eac2.entityActor;
                    }
                }
                entityHitEvent.target = eac.entityActor;
                entityHitEvent.amount = hitEvent.amount;
                entityHitEvent.knockback.set(hitEvent.knockback);
                eac.entityActor.addHitEvent(entityHitEvent);
            }
        }
    }
}
