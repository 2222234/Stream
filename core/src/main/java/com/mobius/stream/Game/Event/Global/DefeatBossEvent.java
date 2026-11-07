package com.mobius.stream.Game.Event.Global;

import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Event.GameEvent;

public class DefeatBossEvent extends GameEvent {
    public final NameType nameType;

    public DefeatBossEvent(NameType nameType) {
        this.nameType = nameType;
    }
}
