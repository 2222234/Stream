package com.mobius.stream.Game.Event.Game.Entity;

import com.mobius.stream.Game.Event.GameEvent;

public class RemoveParticleBoxEvent extends GameEvent {
    public final float x;
    public final float y;

    public RemoveParticleBoxEvent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
