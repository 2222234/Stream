package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;

public class BulletChainSystem extends IteratingSystem {
    private final PoolManager signalPool;
    private final Array<SpawnBulletRequest> spawnQueue;

    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);
    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<LifetimeComponent> lcm = ComponentMapper.getFor(LifetimeComponent.class);

    public BulletChainSystem(PoolManager signalPool, Array<SpawnBulletRequest> spawnQueue) {
        super(Family.all(BulletComponent.class, TransformComponent.class, LifetimeComponent.class).get());
        this.signalPool = signalPool;
        this.spawnQueue = spawnQueue;
    }

    @Override
    protected void processEntity(Entity bullet, float delta) {
        BulletComponent bc = bcm.get(bullet);
        TransformComponent tc = tcm.get(bullet);
        LifetimeComponent lc = lcm.get(bullet);
        if (bc.bulletChain != null) bc.bulletChain.update(signalPool, spawnQueue, bc, tc, lc);
    }
}
