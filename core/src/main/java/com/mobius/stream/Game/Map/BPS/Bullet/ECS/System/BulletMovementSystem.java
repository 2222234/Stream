package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Collision;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.DestroyComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.WallGrid;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Resource.BP.EmitterConfig;

public class BulletMovementSystem extends IteratingSystem {
    private static final float BOUNCE_DAMP = 0.6f;
    private static final float PROBE = 0.5f;

    private final WallGrid wallGrid;
    private final PoolManager signalPool;
    private final Array<FxEvent> fxQueue;

    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

    private final Vector2 hitPoint = new Vector2();
    private final Vector2 lastFree = new Vector2();
    private final Vector2 normal = new Vector2();

    public BulletMovementSystem(WallGrid wallGrid, PoolManager signalPool, Array<FxEvent> fxQueue) {
        super(Family.all(BulletComponent.class, TransformComponent.class).get());
        this.wallGrid = wallGrid;
        this.signalPool = signalPool;
        this.fxQueue = fxQueue;
    }

    @Override
    protected void processEntity(Entity bullet, float delta) {
        BulletComponent bc = bcm.get(bullet);
        TransformComponent tc = tcm.get(bullet);
        bc.time += delta;

        if (!bc.bullet.move) return;

        bc.emitElapsed += delta;

        if (bc.bullet.gravity) tc.vel.y -= 10 * delta;

        float x0 = tc.pos.x, y0 = tc.pos.y;
        float x1 = x0 + tc.vel.x * delta;
        float y1 = y0 + tc.vel.y * delta;

        float hitT = wallGrid.raycastT(x0, y0, x1, y1, hitPoint, lastFree);
        if (hitT < 0f) {
            tc.pos.set(x1, y1);
            return;
        }

        bc.lastHitType = BulletModel.HitType.Wall;

        Collision collision = bc.bullet.collision;
        if (collision != null) {
            switch (collision.collision(bc)) {
                case BulletModel.WallMode.Reflect:
                    reflect(tc, bc, delta, hitT);
                    break;
                case BulletModel.WallMode.Pierce:
                    pierce(tc, bc, x1, y1);
                    break;
                case null:
                    vanish(bullet, tc);
                    break;
                default:
                    vanish(bullet, tc);
                    break;
            }
        } else {
            vanish(bullet, tc);
        }
    }

    private void reflect(TransformComponent tc, BulletComponent bc,
                         float delta, float hitT) {
        computeNormal(hitPoint, tc.vel);

        float dot = tc.vel.x * normal.x + tc.vel.y * normal.y;
        tc.vel.x -= 2f * dot * normal.x;
        tc.vel.y -= 2f * dot * normal.y;
        tc.vel.scl(BOUNCE_DAMP);

        tc.pos.set(lastFree);

        float remaining = (1f - hitT) * delta;
        float rx = tc.pos.x + tc.vel.x * remaining;
        float ry = tc.pos.y + tc.vel.y * remaining;

        if (wallGrid.raycastT(tc.pos.x, tc.pos.y, rx, ry, hitPoint, lastFree) >= 0f) {
            tc.pos.set(lastFree);
        } else {
            tc.pos.set(rx, ry);
        }

        bc.lastHitNormalX = normal.x;
        bc.lastHitNormalY = normal.y;

        bc.alreadyHit.clear();
    }

    private void pierce(TransformComponent tc, BulletComponent bc, float x1, float y1) {
        tc.pos.set(x1, y1);
        bc.lastHitNormalX = 0f;
        bc.lastHitNormalY = 0f;
    }

    private void vanish(Entity bullet, TransformComponent tc) {
        tc.pos.set(lastFree);
        bullet.add(new DestroyComponent());

        FxEvent fx = signalPool.obtain(FxEvent.class);
        fx.set(EmitterConfig.Collision, hitPoint.x, hitPoint.y,
            (float) Math.atan2(-tc.vel.y, -tc.vel.x));
        fxQueue.add(fx);
    }

    private void computeNormal(Vector2 hit, Vector2 vel) {
        boolean leftEmpty = !wallGrid.isSolid(hit.x - PROBE, hit.y);
        boolean rightEmpty = !wallGrid.isSolid(hit.x + PROBE, hit.y);
        boolean downEmpty = !wallGrid.isSolid(hit.x, hit.y - PROBE);
        boolean upEmpty = !wallGrid.isSolid(hit.x, hit.y + PROBE);

        float nx = 0f, ny = 0f;
        if (leftEmpty != rightEmpty) nx = leftEmpty ? -1f : 1f;
        if (downEmpty != upEmpty) ny = downEmpty ? -1f : 1f;

        if (nx == 0f && ny == 0f) {
            if (Math.abs(vel.x) >= Math.abs(vel.y)) nx = vel.x > 0f ? -1f : 1f;
            else ny = vel.y > 0f ? -1f : 1f;
        } else if (nx != 0f && ny != 0f) {
            if (Math.abs(vel.x) >= Math.abs(vel.y)) ny = 0f;
            else nx = 0f;
        }
        normal.set(nx, ny);
    }
}
