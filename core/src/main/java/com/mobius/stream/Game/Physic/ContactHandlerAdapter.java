package com.mobius.stream.Game.Physic;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ContactHandlerAdapter implements ContactHandler {
    @Override
    public void beginContact(Fixture self, Fixture other, Contact contact) {

    }

    @Override
    public void endContact(Fixture self, Fixture other, Contact contact) {

    }

    @Override
    public void preSolve(Fixture self, Fixture other, Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Fixture self, Fixture other, Contact contact, ContactImpulse impulse) {

    }
}
