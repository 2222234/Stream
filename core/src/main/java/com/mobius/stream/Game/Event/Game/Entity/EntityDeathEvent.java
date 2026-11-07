package com.mobius.stream.Game.Event.Game.Entity;

import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.GameEvent;

public class EntityDeathEvent extends GameEvent {
    public EntityActor source;
    public EntityActor target;

    public EntityDeathEvent(EntityActor source, EntityActor target) {
        this.source = source;
        this.target = target;
    }
}
