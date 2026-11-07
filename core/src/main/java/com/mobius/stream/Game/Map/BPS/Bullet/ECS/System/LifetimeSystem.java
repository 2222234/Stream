package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.DestroyComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;

public class LifetimeSystem extends IteratingSystem {
    private final ComponentMapper<LifetimeComponent> lcm = ComponentMapper.getFor(LifetimeComponent.class);

    public LifetimeSystem() {
        super(Family.all(LifetimeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        LifetimeComponent lc = lcm.get(entity);
        lc.remaining -= delta;
        if (lc.remaining <= 0f) {
            entity.add(new DestroyComponent());
        }
    }
}
