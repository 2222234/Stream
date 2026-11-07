package com.mobius.stream.Game.Map.BPS.Bullet.Bullet;

import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;

public interface Trigger {
    boolean fire(BulletComponent bc, LifetimeComponent lc);

    void spawn(BulletModel.Team team, BulletChain bulletChain, Vector2 pos, Vector2 vel, SpawnBulletRequest sbr);
}
