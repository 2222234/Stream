package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;


import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;

public class BulletSpawnSystem extends EntitySystem {
    private final Array<SpawnBulletRequest> spawnQueue;

    public BulletSpawnSystem(Array<SpawnBulletRequest> spawnQueue) {
        this.spawnQueue = spawnQueue;
    }

    @Override
    public void update(float delta) {
        Engine engine = getEngine();
        spawnQueue.iterator().forEach(spawn -> spawnBullet(engine, spawn));
    }

    private void spawnBullet(Engine engine, SpawnBulletRequest req) {
        Entity entity = engine.createEntity();
        Bullet bullet = req.bullet;

        TransformComponent tc = new TransformComponent();
        tc.pos.set(req.x, req.y);
        entity.add(tc);

        tc.vel.set(MathUtils.cos(req.angle) * bullet.speed * req.speedMul,
            MathUtils.sin(req.angle) * bullet.speed * req.speedMul);
        entity.add(tc);

        BulletComponent bc = new BulletComponent();
        bc.bullet = bullet;
        bc.bulletChain = req.bulletChain;
        bc.team = req.team;
        bc.damage = bullet.damage * req.damageMul;
        bc.startX = req.x;
        bc.startY = req.y;
        entity.add(bc);

        entity.add(new LifetimeComponent(bullet.life));

        engine.addEntity(entity);
    }
}
