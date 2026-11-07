package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Emit;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;

public class BulletParticleSystem extends IteratingSystem {
    private final PoolManager signalPool;
    private final Array<FxEvent> fxQueue;

    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);
    private final ComponentMapper<LifetimeComponent> lcm = ComponentMapper.getFor(LifetimeComponent.class);

    public BulletParticleSystem(PoolManager signalPool, Array<FxEvent> fxQueue) {
        super(Family.all(BulletComponent.class, TransformComponent.class, LifetimeComponent.class).get());
        this.signalPool = signalPool;
        this.fxQueue = fxQueue;
    }

    @Override
    protected void processEntity(Entity bullet, float delta) {
        BulletComponent bc = bcm.get(bullet);
        TransformComponent tc = tcm.get(bullet);
        LifetimeComponent lc = lcm.get(bullet);
        Emit emit = bc.bullet.emit;
        if (emit != null) {
            FxEvent event = signalPool.obtain(FxEvent.class);
            if (emit.create(event, tc, bc, lc) != null) {
                fxQueue.add(event);
            } else {
                signalPool.free(event);
            }
        }
    }
}
