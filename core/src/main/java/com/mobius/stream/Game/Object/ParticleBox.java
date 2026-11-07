package com.mobius.stream.Game.Object;

import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Map.BPS.BPSController;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Resource.BP.Particle;

public class ParticleBox {
    public final float x;
    public final float y;
    private final PoolManager signalPool;
    private final BPSController bpsController;
    private final Particle particle;
    private float time;

    public ParticleBox(PoolManager signalPool, BPSController bpsController, Particle particle, float x, float y) {
        this.signalPool = signalPool;
        this.bpsController = bpsController;
        this.particle = particle;
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        time += delta;
        if (time >= particle.duration) {
            FxEvent fxEvent = signalPool.obtain(FxEvent.class);
            fxEvent.emitterConfig = particle.cfg;
            fxEvent.x = x;
            fxEvent.y = y;
            fxEvent.direction = particle.directionRad;
            bpsController.pushFxEvent(fxEvent);
            time = 0;
        }
    }
}
