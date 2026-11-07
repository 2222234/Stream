package com.mobius.stream.Game.Map.BPS.Bullet.Bullet;

public class BulletModel {
    public enum Team {
        Player, Enemy
    }

    public enum WallMode {
        Reflect, Pierce
    }

    public enum Type {
        Bullet, Bomb, Laser
    }

    public enum HitType {
        None, Wall, Enemy, Kill
    }
}
