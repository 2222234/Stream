package com.mobius.stream.Resource;

import com.badlogic.gdx.graphics.g2d.Animation;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL;

public enum Anim {
    DUDE_STAND_LEFT("Dude_Stand_Left", 0, 1, 0.10f, NORMAL, false, 32, 48),
    DUDE_STAND_RIGHT("Dude_Stand_Right", 0, 1, 0.10f, NORMAL, false, 32, 48),
    DUDE_MOVE_LEFT("Dude_Move_Left", 0, 4, 0.10f, LOOP, false, 32, 48),
    DUDE_MOVE_RIGHT("Dude_Move_Right", 0, 4, 0.10f, LOOP, false, 32, 48),
    DUDE_HOLDING_LEFT("Dude_Holding_Left", 0, 4, 0.10f, LOOP, false, 32, 48),
    DUDE_HOLDING_RIGHT("Dude_Holding_Right", 0, 4, 0.10f, LOOP, false, 32, 48),

    GOTO("Bike", 0, 4, 0.10f, LOOP, false, 60, 48),

    Ticket_STAND("Ticket_Idle", 0, 5, 0.10f, LOOP, false, 64, 64),
    Train_STAND("Train_Idle", 0, 5, 0.10f, LOOP, false, 96, 32),
    Clock_STAND("Clock_Idle", 0, 5, 0.10f, LOOP, false, 96, 80),

    Stair_STAND("Stair_Idle", 0, 1, 0.10f, LOOP, false, 64, 64),
    Pos_STAND("Pos_Idle", 0, 1, 0.10f, LOOP, false, 64, 64),
    Hat_STAND("Hat_Idle", 0, 5, 0.10f, LOOP, false, 96, 96),

    Can_STAND("Can_Idle", 0, 5, 0.10f, LOOP, false, 64, 64),
    Tray_STAND("Tray_Idle", 0, 6, 0.10f, LOOP, false, 64, 64),
    Cook_STAND("Cook_Idle", 0, 7, 0.10f, LOOP, false, 96, 96),

    Boss_STAND("Boss_Idle", 0, 5, 0.10f, LOOP, false, 144, 176);

    public final String regionName;
    public final int row;
    public final int frameCount;
    public final float frameDuration;
    public final Animation.PlayMode playMode;
    public final boolean lockable;
    public final int frameW;
    public final int frameH;

    Anim(String regionName, int row, int frameCount, float frameDuration,
         Animation.PlayMode playMode, boolean lockable,
         int frameW, int frameH) {
        this.regionName = regionName;
        this.row = row;
        this.frameCount = frameCount;
        this.frameDuration = frameDuration;
        this.playMode = playMode;
        this.lockable = lockable;
        this.frameW = frameW;
        this.frameH = frameH;
    }
}
