package com.mobius.stream.Resource.Object.Box;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Event.Global.GoToMapEvent;
import com.mobius.stream.Game.Object.TipBox;
import com.mobius.stream.Resource.MapRecord;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.TextRecord;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public class GoTo extends TipBox {
    public GoTo(Context context, Rectangle rect) {
        super(context, rect);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion region = context.resources().getAtlas().findRegion(RegionRecord.Bike);
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
        return TextRecord.Text_GoTo;
    }

    @Override
    public boolean create() {
        return true;
    }

    @Override
    public void interact(Type type) {
        if (type == Type.Use) {
            context.signal().dispatch(new GoToMapEvent(MapRecord.Map.Home));
        }
    }
}
