package com.mobius.stream.Game.Event.Game.Entity;

import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Event.GameEvent;

public class SpawnEntityEvent extends GameEvent {
    public final NameType nameType;
    public final float x;
    public final float y;

    public SpawnEntityEvent(NameType nameType, float x, float y) {
        this.nameType = nameType;
        this.x = x;
        this.y = y;
    }
}
