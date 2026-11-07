package com.mobius.stream.Game.Event.Global;

import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Event.GameEvent;

public class FightBossEvent extends GameEvent {
    public final NameType nameType;

    public FightBossEvent(NameType nameType) {
        this.nameType = nameType;
    }
}
