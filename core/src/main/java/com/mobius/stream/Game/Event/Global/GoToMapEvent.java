package com.mobius.stream.Game.Event.Global;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.MapRecord;

public class GoToMapEvent extends GameEvent {
    public final MapRecord.Map map;

    public GoToMapEvent(MapRecord.Map map) {
        this.map = map;
    }
}
