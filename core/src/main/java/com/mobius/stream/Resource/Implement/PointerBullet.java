package com.mobius.stream.Resource.Implement;

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

public class PointerBullet extends BulletContext {
    public PointerBullet() {
        super(BulletEnum.Normal);
    }

    @Override
    public Bullet getBullet() {
        Bullet bullet = new Bullet(BulletRegion.Pointer_Bullet, BulletModel.Type.Bullet);
        bullet.speed = 8f;
        bullet.radius = 0.5f;
        bullet.damage = 1f;
        bullet.collision = (bulletComponent) -> null;
        return bullet;
    }

    @Override
    public Trigger getTrigger(int index) {
        return new Trigger() {
            @Override
            public boolean fire(BulletComponent bc, LifetimeComponent lc) {
                return false;
            }

            @Override
            public void spawn(BulletModel.Team team, BulletChain bulletChain, Vector2 pos, Vector2 vel, SpawnBulletRequest sbr) {
            }
        };
    }
}
