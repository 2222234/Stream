package com.mobius.stream.Resource.Object.Box;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.Global.FightBossEvent;
import com.mobius.stream.Game.Event.Ui.ClearTipBoxEvent;
import com.mobius.stream.Game.Event.Ui.RefreshTipEvent;
import com.mobius.stream.Game.Map.BPS.Particle.FxEvent;
import com.mobius.stream.Game.Object.TipBox;
import com.mobius.stream.Resource.AudioRecord;
import com.mobius.stream.Resource.BP.EmitterConfig;
import com.mobius.stream.Resource.TextRecord;

public class Computer extends TipBox {
    public Computer(Context context, Rectangle rect) {
        super(context, rect);
    }

    @Override
    public String getShow() {
        return context.playerData().computer == 3 ? TextRecord.Text_Fight : TextRecord.Text_Fix;
    }

    @Override
    public boolean create() {
        return true;
    }

    @Override
    public void interact(Type type) {
        if (type == Type.Use) {
            if (context.playerData().computer1 == 1) {
                context.playerData().computer1 = 2;
                context.playerData().computer++;
                fx();
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().computer2 == 1) {
                context.playerData().computer2 = 2;
                context.playerData().computer++;
                fx();
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().computer3 == 1) {
                context.playerData().computer3 = 2;
                context.playerData().computer++;
                fx();
                context.signal().dispatch(new RefreshTipEvent());
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().computer == 3) {
                context.signal().dispatch(new FightBossEvent(NameType.Computer));
                context.signal().dispatch(new ClearTipBoxEvent(this));
                context.physicService().destroy(body);
            }
        }
    }

    public void fx() {
        FxEvent fxEvent = context.signalPool().obtain(FxEvent.class);
        fxEvent.set(EmitterConfig.Spark, getX() + getWidth() / 2, getY() + getHeight() / 2, MathUtils.HALF_PI);
        context.bpsController().pushFxEvent(fxEvent);
    }
}
