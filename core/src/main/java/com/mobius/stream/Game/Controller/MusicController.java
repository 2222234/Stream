package com.mobius.stream.Game.Controller;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.mobius.stream.Game.Event.Game.Audio.PlayMusicEvent;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.Game.Audio.StopMusicEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.AudioRecord;
import com.mobius.stream.Resource.Resources;

public class MusicController implements Listener<GameEvent>, Disposable {
    private static final String TAG = "MusicController";

    private static final float DEFAULT_FADE_OUT = 0.5f;

    private final Resources resources;

    private Music currentMusic;
    private AudioRecord currentAudioRecord;
    private float currentMusicVolume = 1f;
    private float fadeInTime;
    private float fadeInDuration;

    private Music fadingOutMusic;
    private float fadeOutTime;
    private float fadeOutDuration;
    private float fadeOutStartVolume;

    private float masterVolume = 1f;
    private float musicVolume = 1f;
    private float sfxVolume = 1f;

    public MusicController(Resources resources) {
        this.resources = resources;
    }

    public void onPlayMusic(PlayMusicEvent e) {
        AudioRecord asset = e.audioRecord;
        if (asset == null || !asset.isMusic()) return;

        boolean sameTrack = (asset == currentAudioRecord);
        boolean alreadyPlaying = (currentMusic != null && currentMusic.isPlaying());
        if (!e.restart && sameTrack && alreadyPlaying) return;

        Music next = resources.music(asset);
        if (next == null) {
            Gdx.app.error(TAG, "onPlayMusic: resources.music() returned null");
            return;
        }

        if (next == fadingOutMusic) {
            fadingOutMusic = null;
        }

        if (currentMusic != null && currentMusic != next) {
            float fadeOut = (e.fadeIn > 0f) ? e.fadeIn : DEFAULT_FADE_OUT;
            startFadeOut(currentMusic, fadeOut);
        }

        currentMusic = next;
        currentAudioRecord = asset;
        currentMusicVolume = MathUtils.clamp(e.volume, 0f, 1f);
        fadeInTime = 0f;
        fadeInDuration = Math.max(e.fadeIn, 0f);

        next.stop();
        next.setLooping(e.loop);

        float target = currentMusicVolume * masterVolume * musicVolume;
        if (fadeInDuration > 0f) {
            next.setVolume(0f);
        } else {
            next.setVolume(target);
        }
        next.play();

        if (fadeInDuration > 0f) {
            Gdx.app.log(TAG, "onPlayMusic: started with fade-in " + fadeInDuration
                + "s, MUST call update() every frame or it stays silent");
        }
    }

    public void startFadeOut(Music music, float duration) {
        if (music == null) return;

        if (fadingOutMusic != null && fadingOutMusic != music) {
            fadingOutMusic.stop();
            fadingOutMusic = null;
        }

        float dur = Math.max(0f, duration);
        if (dur <= 0f) {
            music.stop();
            if (music == fadingOutMusic) fadingOutMusic = null;
            return;
        }

        fadingOutMusic = music;
        fadeOutTime = 0f;
        fadeOutDuration = dur;
        fadeOutStartVolume = music.getVolume();
    }

    public void stopMusic(float fadeOut) {
        if (currentMusic != null) {
            startFadeOut(currentMusic, fadeOut);
            currentMusic = null;
            currentAudioRecord = null;
            fadeInTime = 0f;
            fadeInDuration = 0f;
        }
    }

    public void onPlaySound(PlaySoundEvent e) {
        AudioRecord asset = e.audioRecord;
        if (asset == null || !asset.isSound()) return;

        float volume = MathUtils.clamp(e.volume * masterVolume * sfxVolume, 0f, 1f);
        if (volume <= 0f) return;

        Sound sound = resources.sound(asset);
        if (sound == null) {
            Gdx.app.error(TAG, "onPlaySound: resources.sound() returned null");
            return;
        }

        sound.play(
            volume,
            MathUtils.clamp(e.pitch, 0.5f, 2f),
            MathUtils.clamp(e.pan, -1f, 1f));
    }

    public void update(float delta) {
        if (delta < 0f) delta = 0f;

        if (fadingOutMusic != null) {
            if (fadeOutDuration <= 0f) {
                fadingOutMusic.stop();
                fadingOutMusic = null;
            } else {
                fadeOutTime += delta;
                float t = MathUtils.clamp(fadeOutTime / fadeOutDuration, 0f, 1f);
                fadingOutMusic.setVolume(fadeOutStartVolume * (1f - t));
                if (t >= 1f) {
                    fadingOutMusic.stop();
                    fadingOutMusic = null;
                }
            }
        }

        if (currentMusic != null && currentMusic != fadingOutMusic) {
            float target = currentMusicVolume * masterVolume * musicVolume;
            if (fadeInDuration > 0f && fadeInTime < fadeInDuration) {
                fadeInTime += delta;
                float t = MathUtils.clamp(fadeInTime / fadeInDuration, 0f, 1f);
                currentMusic.setVolume(target * t);
            } else if (Math.abs(currentMusic.getVolume() - target) > 1e-4f) {
                currentMusic.setVolume(target);
            }
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float v) {
        masterVolume = MathUtils.clamp(v, 0f, 1f);
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float v) {
        musicVolume = MathUtils.clamp(v, 0f, 1f);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(float v) {
        sfxVolume = MathUtils.clamp(v, 0f, 1f);
    }

    public AudioRecord getCurrentMusicAsset() {
        return currentAudioRecord;
    }

    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent event) {
        if (event instanceof PlayMusicEvent) {
            onPlayMusic((PlayMusicEvent) event);
        }
        if (event instanceof PlaySoundEvent) {
            onPlaySound((PlaySoundEvent) event);
        }
        if (event instanceof StopMusicEvent stopMusicEvent) {
            stopMusic(stopMusicEvent.fadeOut);
        }
    }

    @Override
    public void dispose() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
        if (fadingOutMusic != null) {
            fadingOutMusic.stop();
            fadingOutMusic = null;
        }
        currentAudioRecord = null;
        fadeInDuration = 0f;
        fadeInTime = 0f;
        fadeOutDuration = 0f;
        fadeOutTime = 0f;
    }
}
