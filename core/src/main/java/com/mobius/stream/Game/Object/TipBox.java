package com.mobius.stream.Game.Object;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.mobius.stream.Game.Context;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.Ui.TipEvent;
import com.mobius.stream.Game.Physic.BodyFactory;
import com.mobius.stream.Game.Physic.ContactHandler;

import static com.mobius.stream.Resource.MapRecord.toMeters;

public abstract class TipBox extends Actor implements ContactHandler, Interact {
    protected final Context context;
    protected Body body;

    public TipBox(Context context, Rectangle rect) {
        this.context = context;
        setSize(toMeters(rect.width), toMeters(rect.height));
        setPosition(toMeters(rect.x), toMeters(rect.y), Align.bottomLeft);
    }

    @Override
    public void beginContact(Fixture self, Fixture other, Contact contact) {
        if (other.getBody().getUserData() instanceof EntityActor entityActor) {
            if (entityActor.getAttribute().nameType() == NameType.Dude) {
                if (other.getUserData() == BodyFactory.Type.HitBoxSensor) {
                    context.signal().dispatch(new TipEvent(this, self.getBody().getPosition(), true));
                }
            }
        }
    }

    @Override
    public void endContact(Fixture self, Fixture other, Contact contact) {
        if (other.getBody().getUserData() instanceof EntityActor entityActor) {
            if (entityActor.getAttribute().nameType() == NameType.Dude) {
                if (other.getUserData() == BodyFactory.Type.HitBoxSensor) {
                    context.signal().dispatch(new TipEvent(this, self.getBody().getPosition(), false));
                }
            }
        }
    }

    @Override
    public void preSolve(Fixture self, Fixture other, Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Fixture self, Fixture other, Contact contact, ContactImpulse impulse) {

    }

    public abstract String getShow();

    public abstract boolean create();

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
