package com.mobius.stream.Game.Animate;

import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Entity.EntityState;

public interface StateLogic {
    EntityState decide(float delta, EntityActor self, EntityState current);
}
