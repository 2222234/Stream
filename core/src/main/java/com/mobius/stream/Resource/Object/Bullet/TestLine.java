package com.mobius.stream.Resource.Object.Bullet;

import com.mobius.stream.Game.Object.GunFactory.ChainContext;

public class TestLine {
    public static ChainContext chainContextNormal() {
        ChainContext chainContext = new ChainContext(null, new EmitterBullet());
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 1);
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 2);
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 3);
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 4);
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 5);
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 6);
        chainContext.setChain(new ChainContext(chainContext, new BallisticBullet()), 7);
        return chainContext;
    }
}
