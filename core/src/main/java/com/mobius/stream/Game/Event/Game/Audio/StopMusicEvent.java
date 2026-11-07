package com.mobius.stream.Game.Event.Game.Audio;

import com.mobius.stream.Game.Event.GameEvent;

public class StopMusicEvent extends GameEvent {
    public final float fadeOut;

    public StopMusicEvent(float fadeOut) {
        this.fadeOut = fadeOut;
    }
}
