package com.mobius.stream.Game.Event.Game.Audio;

import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.AudioRecord;

public class PlaySoundEvent extends GameEvent {
    public final AudioRecord audioRecord;
    public final float volume;
    public final float pitch;
    public final float pan;

    public PlaySoundEvent(AudioRecord audioRecord, float volume, float pitch, float pan) {
        this.audioRecord = audioRecord;
        this.volume = volume;
        this.pitch = pitch;
        this.pan = pan;
    }

    public static PlaySoundEvent varied(AudioRecord audioRecord, float volume, float minPitch, float maxPitch) {
        float p = minPitch + (float) Math.random() * (maxPitch - minPitch);
        return new PlaySoundEvent(audioRecord, volume, p, 0.0F);
    }
}

