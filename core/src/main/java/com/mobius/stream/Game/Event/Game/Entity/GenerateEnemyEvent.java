package com.mobius.stream.Game.Event.Game.Entity;

import com.mobius.stream.Game.Event.GameEvent;

public class GenerateEnemyEvent extends GameEvent {
    public final boolean generate;

    public GenerateEnemyEvent(boolean generate) {
        this.generate = generate;
    }
}
