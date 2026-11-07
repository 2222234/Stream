package com.mobius.stream.Resource.Implement;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletModel;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Trigger;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.BulletComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.ECS.Component.Life.LifetimeComponent;
import com.mobius.stream.Game.Map.BPS.Bullet.Event.SpawnBulletRequest;
import com.mobius.stream.Game.Object.GunFactory.BulletContext;
import com.mobius.stream.Resource.BP.BulletRegion;
import com.mobius.stream.Resource.Object.Bullet.BulletEnum;

public class CoinBullet extends BulletContext {
    public CoinBullet() {
        super(BulletEnum.Bomb);
    }

    @Override
    public Bullet getBullet() {
        Bullet bullet = new Bullet(BulletRegion.Coin_Bullet, BulletModel.Type.Bomb);
        bullet.gravity = true;
        bullet.speed = 8f;
        bullet.radius = 5f;
        bullet.damage = 5f;
        bullet.collision = (bulletComponent) -> BulletModel.WallMode.Reflect;
        return bullet;
    }

    @Override
    public Trigger getTrigger(int index) {
        return new Trigger() {
            @Override
            public boolean fire(BulletComponent bc, LifetimeComponent lc) {
                return lc.remaining <= 0;
            }

            @Override
            public void spawn(BulletModel.Team team, BulletChain bulletChain, Vector2 pos, Vector2 vel, SpawnBulletRequest sbr) {
                sbr.set(team, bulletChain, bulletChain.bullet, pos.x, pos.y, vel.angleRad() + index * MathUtils.PI2 / 8, 1, 1);
            }
        };
    }
}
