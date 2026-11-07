package com.mobius.stream.Resource.Object.Box;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.Global.FightBossEvent;
import com.mobius.stream.Game.Event.Ui.ClearTipBoxEvent;
import com.mobius.stream.Game.Event.Ui.RefreshTipEvent;
import com.mobius.stream.Game.Object.TipBox;
import com.mobius.stream.Resource.AudioRecord;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.TextRecord;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public class Clock extends TipBox {
    public Clock(Context context, Rectangle rect) {
        super(context, rect);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        String src = RegionRecord.Dial;
        if (context.playerData().clock == 1) src = RegionRecord.Dial1;
        if (context.playerData().clock == 2) src = RegionRecord.Dial2;
        if (context.playerData().clock == 3) src = RegionRecord.Dial3;
        TextureRegion region = context.resources().getAtlas().findRegion(src);
        float width = toMeters(region.getRegionWidth());
        float height = toMeters(region.getRegionHeight());
        batch.draw(region, getX(), getY(),
            0, 0,
            width, height,
            1f, 1f,
            getRotation());
    }

    @Override
    public String getShow() {
        return context.playerData().clock == 3 ? TextRecord.Text_Fight : TextRecord.Text_Place;
    }

    @Override
    public boolean create() {
        return true;
    }

    @Override
    public void interact(Type type) {
        if (type == Type.Use) {
            if (context.playerData().pointer1 == 1) {
                context.playerData().pointer1 = 2;
                context.playerData().clock++;
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().pointer2 == 1) {
                context.playerData().pointer2 = 2;
                context.playerData().clock++;
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().pointer3 == 1) {
                context.playerData().pointer3 = 2;
                context.playerData().clock++;
                context.signal().dispatch(new RefreshTipEvent());
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().clock == 3) {
                context.signal().dispatch(new FightBossEvent(NameType.Clock));
                context.signal().dispatch(new ClearTipBoxEvent(this));
                context.physicService().destroy(body);
            }
        }
    }
}
