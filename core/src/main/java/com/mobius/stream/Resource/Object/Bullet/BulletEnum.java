package com.mobius.stream.Resource.Object.Bullet;

import com.mobius.stream.Game.Object.GunFactory.BulletContext;
import com.mobius.stream.Resource.TextRecord;

public enum BulletEnum {
    Normal(false, 0, TextRecord.Text_Normal, NormalBullet.class),
    Splitter(true, 50, TextRecord.Text_Splitter, SplitterBullet.class),
    Bouncy(false, 50, TextRecord.Text_Bouncy, BouncyBullet.class),
    Bomb(true, 500, TextRecord.Text_Bomb, BombBullet.class),
    Dropper(true, 1000, TextRecord.Text_Dropper, DropperBullet.class),
    Emitter(true, 1000, TextRecord.Text_Emitter, EmitterBullet.class),
    Ballistic(false, 500, TextRecord.Text_Ballistic, BallisticBullet.class),
    Fixed(false, 500, TextRecord.Text_Fixed, FixedBullet.class);
    public final boolean chain;
    public final int cost;
    public final String src;
    public final Class<? extends BulletContext> bulletContext;

    BulletEnum(boolean chain, int cost, String src, Class<? extends BulletContext> bulletContext) {
        this.chain = chain;
        this.cost = cost;
        this.src = src;
        this.bulletContext = bulletContext;
    }
}
