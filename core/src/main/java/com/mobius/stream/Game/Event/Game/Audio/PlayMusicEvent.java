package com.mobius.stream.Game.Event.Game.Audio;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.AudioRecord;

public class PlayMusicEvent extends GameEvent {
    public final AudioRecord audioRecord;
    public final boolean loop;
    public final float volume;
    public final float fadeIn;
    public final boolean restart;

    public PlayMusicEvent(AudioRecord audioRecord, boolean loop, float volume, float fadeIn, boolean restart) {
        this.audioRecord = audioRecord;
        this.loop = loop;
        this.volume = volume;
        this.fadeIn = fadeIn;
        this.restart = restart;
    }
}
