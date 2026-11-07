package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;

public class PoolSystem extends EntitySystem {
    private final PoolManager poolManager;
    private final Array<HitEvent> hitQueue;
    private final Array<SpawnBulletRequest> spawnQueue;

    public PoolSystem(PoolManager poolManager, Array<SpawnBulletRequest> spawnQueue, Array<HitEvent> hitQueue) {
        this.poolManager = poolManager;
        this.spawnQueue = spawnQueue;
        this.hitQueue = hitQueue;
    }

    @Override
    public void update(float delta) {
        hitQueue.iterator().forEach(poolManager::free);
        hitQueue.clear();
        spawnQueue.iterator().forEach(poolManager::free);
        spawnQueue.clear();
    }
}
