package com.mobius.stream.Game.Event.Game.Map;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.MapRecord;

public class ChangeMapEvent extends GameEvent {
    public final MapRecord.Map map;

    public ChangeMapEvent(MapRecord.Map map) {
        this.map = map;
    }
}
