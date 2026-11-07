package com.mobius.stream.Resource.Object.Bullet;

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
import com.mobius.stream.Resource.BP.EmitterConfig;

public class BouncyBullet extends BulletContext {
    public BouncyBullet() {
        super(BulletEnum.Bouncy);
    }

    @Override
    public Bullet getBullet() {
        Bullet bullet = new Bullet(BulletRegion.Bullet_Bouncy, BulletModel.Type.Bullet);
        bullet.gravity = false;
        bullet.life = 1f;
        bullet.speed = 100f;
        bullet.radius = 0.5f;
        bullet.emit = (fxEvent, tc, bc, lc) -> {
            fxEvent.emitterConfig = EmitterConfig.FireSpark;
            fxEvent.x = tc.pos.x;
            fxEvent.y = tc.pos.y;
            fxEvent.direction = (float) Math.PI + tc.vel.angleRad();
            bc.emitElapsed = 0;
            return fxEvent;
        };
        bullet.collision = bc -> BulletModel.WallMode.Reflect;
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
