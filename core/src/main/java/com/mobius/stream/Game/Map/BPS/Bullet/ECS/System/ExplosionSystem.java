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
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.DestroyComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.HealthComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.HitEvent;
import com.mobius.stream.Game.Map.BPS.Bullet.Tool.SpatialHashGrid;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Resource.BP.EmitterConfig;

public class ExplosionSystem extends IteratingSystem {
    private final SpatialHashGrid<Entity> grid;
    private final PoolManager signalPool;
    private final Array<FxEvent> fxQuene;
    private final Array<HitEvent> hitQueue;

    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);
    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<HurtboxComponent> hbcm = ComponentMapper.getFor(HurtboxComponent.class);
    private final ComponentMapper<LifetimeComponent> ltcm = ComponentMapper.getFor(LifetimeComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);

    private final Array<Entity> nearby = new Array<>();

    public ExplosionSystem(SpatialHashGrid<Entity> grid, PoolManager signalPool, Array<HitEvent> hitQueue, Array<FxEvent> fxQuene) {
        super(Family.all(BulletComponent.class, TransformComponent.class, LifetimeComponent.class).get());
        this.grid = grid;
        this.hitQueue = hitQueue;
        this.signalPool = signalPool;
        this.fxQuene = fxQuene;
    }

    @Override
    protected void processEntity(Entity bullet, float delta) {
        BulletComponent bc = bcm.get(bullet);
        if (bc.bullet.type != BulletModel.Type.Bomb) return;
        if (bc.damageApplied) return;

        LifetimeComponent lc = ltcm.get(bullet);
        if (lc.remaining > 0f) return;

        TransformComponent tc = tcm.get(bullet);
        float radius = bc.bullet.radius;

        nearby.clear();
        grid.query(tc.pos.x, tc.pos.y, radius, nearby);

        for (Entity target : nearby) {
            if (target == bullet) continue;

            HurtboxComponent hbc = hbcm.get(target);
            if (hbc == null) continue;
            if (hbc.team == bc.team) continue;

            HealthComponent hc = hcm.get(target);
            if (hc != null && hc.dead) continue;

            TransformComponent tc2 = tcm.get(target);
            float dx = tc.pos.x - tc2.pos.x;
            float dy = tc.pos.y - tc2.pos.y;
            float r = radius + hbc.radius;
            if (dx * dx + dy * dy > r * r) continue;

            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float falloff = 1f - Math.min(1f, dist / radius) * 0.5f;

            HitEvent hitEvent = signalPool.obtain(HitEvent.class);
            hitEvent.set(bullet, target, bc.damage * falloff);
            hitEvent.knockback.set(tc2.pos).sub(tc.pos).nor();
            hitEvent.knockback.scl(bc.bullet.knockbackForce);
            hitQueue.add(hitEvent);
        }

        bc.damageApplied = true;

        FxEvent fxEvent = signalPool.obtain(FxEvent.class);
        fxEvent.set(EmitterConfig.Explosion, tc.pos.x, tc.pos.y, 0f);
        fxQuene.add(fxEvent);

        bullet.add(new DestroyComponent());
    }
}
