package com.mobius.stream.Resource.Player;

import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;
import com.mobius.stream.Game.Object.GunFactory.ChainContext;

public class PlayerData {
    public int cost;
    public ChainContext weapon1;
    public ChainContext weapon2;

    public boolean stage1;
    public int pointer1;
    public int pointer2;
    public int pointer3;
    public int clock;
    public boolean generate1;
    public boolean boss1;

    public boolean stage2;
    public int hat1;
    public int hanger;
    public boolean generate2;
    public boolean boss2;

    public boolean stage3;
    public int meat;
    public int naiEgg;
    public int window;
    public boolean generate3;
    public boolean boss3;

    public int computer1;
    public int computer2;
    public int computer3;
    public int computer;
    public boolean boss4;

    public transient EntityActor player;
    public transient BulletChain bulletChain1;
    public transient BulletChain bulletChain2;

    public boolean start;

    public PlayerData() {
    }

    public float getMaxHp() {
        if (player == null) return 0;
        return player.getAttribute().blood();
    }

    public float getHp() {
        if (player == null) return 0;
        return player.getBlood();
    }

    public int getHolding() {
        if (player == null) return 0;
        return player.getHolding();
    }
}

