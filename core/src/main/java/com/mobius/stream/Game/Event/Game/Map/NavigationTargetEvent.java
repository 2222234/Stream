package com.mobius.stream.Game.Event.Game.Map;

import com.mobius.stream.Game.Event.GameEvent;

public class NavigationTargetEvent extends GameEvent {
    public final float x;
    public final float y;

    public NavigationTargetEvent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
