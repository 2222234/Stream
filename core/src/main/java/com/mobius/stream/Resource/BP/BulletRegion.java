package com.mobius.stream.Resource.BP;

public enum BulletRegion {
    Bullet(false), Laser(true),
    Bullet_Normal(false), Bullet_Bouncy(false), Bullet_Splitter(false),
    Bullet_Bomb(false), Bullet_Dropper(false), Bullet_Emitter(false),
    Bullet_Laser(true),
    Ticket_Bullet(false), Wheel_Bullet(false), Pointer_Bullet(false),
    Gear_Bullet(false), Hat_Bullet(false), Coin_Bullet(false),
    Meat_Bullet(false), Trash_Bullet(false), Flour_Bullet(false),
    Sprite_Bullet(false),
    ;
    public final boolean isLaser;

    BulletRegion(boolean isLaser) {
        this.isLaser = isLaser;
    }
}
