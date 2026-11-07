package com.mobius.stream.Resource;

public enum AudioRecord {
    Title("Audio/Title.mp3", Kind.MUSIC),
    Station("Audio/Station.mp3", Kind.MUSIC),
    Mall("Audio/Mall.mp3", Kind.MUSIC),
    School("Audio/School.mp3", Kind.MUSIC),
    LastBoss("Audio/LastBoss.mp3", Kind.MUSIC),

    //    Text("Audio/Text.wav", Kind.SOUND),
    PickUp("Audio/PickUp.wav", Kind.SOUND),
    FootStep("Audio/FootStep.wav", Kind.SOUND),
    Hit("Audio/Hit.wav", Kind.SOUND),
    Bullet("Audio/Bullet.wav", Kind.SOUND),
    Laser("Audio/Laser.wav", Kind.SOUND);

    public final String path;
    public final Kind kind;

    AudioRecord(String path, Kind kind) {
        this.path = path;
        this.kind = kind;
    }

    public boolean isMusic() {
        return this.kind == Kind.MUSIC;
    }

    public boolean isSound() {
        return this.kind == Kind.SOUND;
    }

    public enum Kind {
        MUSIC,
        SOUND;

        Kind() {
        }
    }
}
