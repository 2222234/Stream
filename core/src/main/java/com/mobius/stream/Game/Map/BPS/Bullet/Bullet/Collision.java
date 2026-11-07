package com.mobius.stream.Game.Map.BPS.Bullet.Bullet;

import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;

public interface Collision {
    BulletModel.WallMode collision(BulletComponent bc);
}
