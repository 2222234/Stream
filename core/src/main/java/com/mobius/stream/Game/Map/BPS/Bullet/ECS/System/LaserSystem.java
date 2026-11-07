package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.HurtboxComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.HealthComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.SpatialHashGrid;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.WallGrid;

public class LaserSystem extends IteratingSystem {
    private final WallGrid wallGrid;
    private final SpatialHashGrid<Entity> grid;
    private final PoolManager signalPool;
    private final Array<HitEvent> hitQueue;

    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);
    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<HurtboxComponent> hbcm = ComponentMapper.getFor(HurtboxComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);

    private final Array<Entity> nearby = new Array<>();
    private final Vector2 end = new Vector2();
    private final Vector2 hit = new Vector2();
    private final Vector2 temp = new Vector2();

    public LaserSystem(SpatialHashGrid<Entity> grid, WallGrid wallGrid, PoolManager signalPool, Array<HitEvent> hitQueue) {
        super(Family.all(BulletComponent.class, TransformComponent.class).get());
        this.grid = grid;
        this.wallGrid = wallGrid;
        this.signalPool = signalPool;
        this.hitQueue = hitQueue;
    }

    private static float segPointDist(float ax, float ay, float bx, float by, float px, float py) {
        float abx = bx - ax, aby = by - ay;
        float apx = px - ax, apy = py - ay;
        float ab2 = abx * abx + aby * aby;
        if (ab2 < 1e-9f) {
            return (float) Math.sqrt(apx * apx + apy * apy);
        }
        float t = (apx * abx + apy * aby) / ab2;
        if (t < 0f) t = 0f;
        else if (t > 1f) t = 1f;
        float cx = ax + abx * t;
        float cy = ay + aby * t;
        float dx = px - cx, dy = py - cy;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    protected void processEntity(Entity bullet, float delta) {
        BulletComponent bc = bcm.get(bullet);
        if (bc.bullet.type != BulletModel.Type.Laser) return;

        TransformComponent tc = tcm.get(bullet);

        end.set(tc.vel).nor().scl(bc.bullet.laserLength).add(tc.pos);
        if (bc.bullet.laserBlockedByWall) {
            if (wallGrid.raycast(tc.pos.x, tc.pos.y, end.x, end.y, hit)) {
                end.set(hit);
            }
        }
        bc.endLength = temp.set(end).sub(tc.pos).len();

        float interval = bc.bullet.hitInterval;
        if (interval > 0f) {
            bc.timer -= delta;
            if (bc.timer > 0f) return;
            bc.timer = interval;
        } else if (bc.damageApplied) {
            return;
        }

        float minX = Math.min(tc.pos.x, end.x) - bc.bullet.radius - 1f;
        float maxX = Math.max(tc.pos.x, end.x) + bc.bullet.radius + 1f;
        float minY = Math.min(tc.pos.y, end.y) - bc.bullet.radius - 1f;
        float maxY = Math.max(tc.pos.y, end.y) + bc.bullet.radius + 1f;

        float cx = (minX + maxX) * 0.5f;
        float cy = (minY + maxY) * 0.5f;
        float queryR = Math.max(maxX - minX, maxY - minY) * 0.5f;

        nearby.clear();
        grid.query(cx, cy, queryR, nearby);

        for (Entity target : nearby) {
            if (target == bullet) continue;

            HurtboxComponent hbc = hbcm.get(target);
            if (hbc == null) continue;
            if (hbc.team == bc.team) continue;

            HealthComponent hc = hcm.get(target);
            if (hc != null && hc.dead) continue;

            TransformComponent tc2 = tcm.get(target);
            float dist = segPointDist(tc.pos.x, tc.pos.y, end.x, end.y, tc2.pos.x, tc2.pos.y);
            if (dist > bc.bullet.radius + hbc.radius) continue;

            HitEvent he = signalPool.obtain(HitEvent.class);
            he.knockback.set(tc.vel).nor().scl(bc.bullet.knockbackForce);
            he.set(bullet, target, bc.damage);
            hitQueue.add(he);
        }

        bc.damageApplied = true;
    }
}
