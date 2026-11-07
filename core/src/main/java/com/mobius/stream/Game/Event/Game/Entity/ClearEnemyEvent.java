package com.mobius.stream.Game.Event.Game.Entity;

import com.mobius.stream.Game.Event.GameEvent;

public class ClearEnemyEvent extends GameEvent {
    public final boolean fx;

    public ClearEnemyEvent(boolean fx) {
        this.fx = fx;
    }
}
