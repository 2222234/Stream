package com.mobius.stream.Game.Object;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mobius.stream.Game.Entity.Attribute.NameType;
import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Ui.ShowTitleEvent;
import com.mobius.stream.Game.Physic.ContactHandlerAdapter;
import com.mobius.stream.Stage.Component.ToastOverlay;

public class RoadSign extends ContactHandlerAdapter {
    private final Signal<GameEvent> signal;
    private final String name;

    public RoadSign(Signal<GameEvent> signal, String name) {
        this.signal = signal;
        this.name = name;
    }

    @Override
    public void beginContact(Fixture self, Fixture other, Contact contact) {
        if (other.getBody().getUserData() instanceof EntityActor entityActor) {
            if (entityActor.getAttribute().nameType() == NameType.Dude) {
                signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Main, name, 1));
            }
        }
    }
}
