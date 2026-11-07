package com.mobius.stream.Game.Object.GunFactory;

import com.mobius.stream.Game.Map.BPS.Bullet.Bullet.BulletChain;

public class ChainContext {
    public transient ChainContext upper;
    public ChainContext[] lower;
    public BulletContext bullet;
    public int cost;

    public ChainContext(ChainContext upper, BulletContext bullet) {
        this.upper = upper;
        this.bullet = bullet;
        cost = bullet.bulletEnum.cost;
        lower = new ChainContext[8];
    }

    public ChainContext() {
        lower = new ChainContext[8];
    }

    public BulletChain buildChain() {
        BulletChain chain = new BulletChain(bullet.getBullet());
        int index = 0;
        for (ChainContext chainContext : lower) {
            if (chainContext != null) {
                chain.addBullet(chainContext.buildChain(), bullet.getTrigger(index));
            }
            index++;
        }
        return chain;
    }

    public void cost(int cost) {
        this.cost += cost;
        if (upper != null) upper.cost(cost);
    }

    public void setChain(ChainContext lower, int index) {
        if (lower != null) {
            cost(lower.cost);
        } else {
            cost(-this.lower[index].cost);
        }
        this.lower[index] = lower;
    }

    public boolean isChain() {
        return bullet.bulletEnum.chain;
    }
}
