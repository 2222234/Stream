package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.HurtboxComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.HealthComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.SpatialHashGrid;

public class BulletSystem extends IteratingSystem {
    private final SpatialHashGrid<Entity> grid;
    private final PoolManager poolSignal;
    private final Array<HitEvent> hitQueue;

    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);
    private final ComponentMapper<HurtboxComponent> hbcm = ComponentMapper.getFor(HurtboxComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);

    private final Array<Entity> nearby = new Array<>();

    public BulletSystem(SpatialHashGrid<Entity> grid, PoolManager poolSignal, Array<HitEvent> hitQueue) {
        super(Family.all(BulletComponent.class, TransformComponent.class).get());
        this.grid = grid;
        this.poolSignal = poolSignal;
        this.hitQueue = hitQueue;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BulletComponent bc = bcm.get(entity);
        if (bc == null) return;
        if (bc.bullet.type != BulletModel.Type.Bullet) return;
        TransformComponent tc = tcm.get(entity);

        nearby.clear();
        grid.query(tc.pos.x, tc.pos.y, bc.bullet.radius, nearby);

        for (Entity target : nearby) {
            if (target == entity) continue;

            HurtboxComponent hbc = hbcm.get(target);
            if (hbc == null) continue;
            if (hbc.team == bc.team) continue;

            if (bc.alreadyHit.contains(target)) continue;

            HealthComponent hc = hcm.get(target);
            if (hc != null && hc.dead) continue;

            TransformComponent tc2 = tcm.get(target);
            float dx = tc.pos.x - tc2.pos.x;
            float dy = tc.pos.y - tc2.pos.y;
            float r = bc.bullet.radius + hbc.radius;
            if (dx * dx + dy * dy > r * r) continue;

            bc.alreadyHit.add(target);
            bc.lastHitType = BulletModel.HitType.Enemy;

            HitEvent hitEvent = poolSignal.obtain(HitEvent.class);
            hitEvent.set(entity, target, bc.damage);

            if (bc.bullet.knockbackForce > 0f) {
                hitEvent.knockback.set(tc.vel).nor();
                hitEvent.knockback.scl(bc.bullet.knockbackForce);
            }

            hitQueue.add(hitEvent);

            if (hc != null && hc.hp - bc.damage <= 0f) {
                bc.lastHitType = BulletModel.HitType.Kill;
            }
        }
    }
}
