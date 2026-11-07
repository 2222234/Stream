package com.mobius.stream.Game.Event.Game.Entity;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.BP.Particle;

public class CreateParticleBoxEvent extends GameEvent {
    public final Particle particle;
    public final float x;
    public final float y;

    public CreateParticleBoxEvent(Particle particle, float x, float y) {
        this.particle = particle;
        this.x = x;
        this.y = y;
    }
}
