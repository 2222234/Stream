package com.mobius.stream.Resource.BP;

import com.badlogic.gdx.math.MathUtils;

public enum Particle {
    Fountain(EmitterConfig.Fountain, 0.2f, MathUtils.HALF_PI),
    Spark(EmitterConfig.Spark, 0.2f, MathUtils.HALF_PI);
    public final EmitterConfig cfg;
    public final float duration;
    public final float directionRad;

    Particle(EmitterConfig cfg, float duration, float directionRad) {
        this.cfg = cfg;
        this.duration = duration;
        this.directionRad = directionRad;
    }
}
