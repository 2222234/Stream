package com.mobius.stream.Game.Object.GunFactory;

import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Bullet;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.Trigger;
import com.mobius.stream.Resource.Object.Bullet.BulletEnum;

public abstract class BulletContext {
    public final BulletEnum bulletEnum;

    protected BulletContext(BulletEnum bulletEnum) {
        this.bulletEnum = bulletEnum;
    }

    public abstract Bullet getBullet();

    public abstract Trigger getTrigger(int index);
}
