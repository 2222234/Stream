package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Stage.Component.FadeOverlay;

public class FadeOverlayEvent extends GameEvent {
    public final FadeOverlay.FadeState fadeState;
    public final float time;

    public FadeOverlayEvent(FadeOverlay.FadeState fadeState, float time) {
        this.fadeState = fadeState;
        this.time = time;
    }
}
