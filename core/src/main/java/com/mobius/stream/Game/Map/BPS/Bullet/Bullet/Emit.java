package com.mobius.stream.Game.Map.BPS.Bullet.Bullet;

import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Basic.TransformComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;

public interface Emit {
    FxEvent create(FxEvent fxEvent, TransformComponent tc, BulletComponent bc, LifetimeComponent lc);
}
