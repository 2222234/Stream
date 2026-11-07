package com.mobius.stream.Game.Map.BPS.Particle;

import com.badlogic.gdx.utils.Pool;
import com.mobius.stream.Resource.BP.EmitterConfig;

public class FxEvent implements Pool.Poolable {
    public EmitterConfig emitterConfig;
    public float x, y;
    public float direction;

    public FxEvent set(EmitterConfig emitterConfig, float x, float y, float direction) {
        this.emitterConfig = emitterConfig;
        this.x = x;
        this.y = y;
        this.direction = direction;
        return this;
    }

    @Override
    public void reset() {
        emitterConfig = null;
        x = 0f;
        y = 0f;
        direction = 0f;
    }
}
