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

public class Hanger extends TipBox {
    public Hanger(Context context, Rectangle rect) {
        super(context, rect);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        String src = RegionRecord.Hanger;
        if (context.playerData().hanger == 1) src = RegionRecord.Hanger1;
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
        return context.playerData().hanger == 1 ? TextRecord.Text_Fight : TextRecord.Text_Place;
    }

    @Override
    public boolean create() {
        return true;
    }

    @Override
    public void interact(Type type) {
        if (type == Type.Use) {
            if (context.playerData().hat1 == 1) {
                context.playerData().hat1 = 2;
                context.playerData().hanger++;
                context.signal().dispatch(new RefreshTipEvent());
                context.signal().dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
                return;
            }
            if (context.playerData().hanger == 1) {
                context.signal().dispatch(new FightBossEvent(NameType.Hat));
                context.signal().dispatch(new ClearTipBoxEvent(this));
                context.physicService().destroy(body);
            }
        }
    }
}
