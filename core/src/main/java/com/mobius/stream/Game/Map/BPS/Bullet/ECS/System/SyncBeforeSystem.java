package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.EntityActorComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.HealthComponent;

public class SyncBeforeSystem extends IteratingSystem {
    private final ComponentMapper<TransformComponent> tcm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<EntityActorComponent> eacm = ComponentMapper.getFor(EntityActorComponent.class);

    public SyncBeforeSystem() {
        super(Family.all(TransformComponent.class, HealthComponent.class, EntityActorComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        TransformComponent tc = tcm.get(entity);
        HealthComponent hc = hcm.get(entity);
        EntityActorComponent eac = eacm.get(entity);
        tc.pos.set(eac.entityActor.getPosition());
        hc.hp = eac.entityActor.getBlood();
    }
}
