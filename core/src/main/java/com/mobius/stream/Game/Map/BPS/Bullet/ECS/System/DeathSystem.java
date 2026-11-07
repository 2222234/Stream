package com.mobius.stream.Game.Map.BPS.Bullet.ECS.System;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.DestroyComponent;

public class DeathSystem extends EntitySystem {
    @Override
    public void update(float delta) {
        Engine engine = getEngine();
        Array<Entity> toRemove = new Array<>();

        for (Entity e : engine.getEntitiesFor(Family.all(DestroyComponent.class).get())) {
            toRemove.add(e);
        }

        for (Entity e : toRemove) {
            engine.removeEntity(e);
        }
    }
}

