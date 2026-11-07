package com.mobius.stream.Game.Physic;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public interface ContactHandler {
    void beginContact(Fixture self, Fixture other, Contact contact);

    void endContact(Fixture self, Fixture other, Contact contact);

    void preSolve(Fixture self, Fixture other, Contact contact, Manifold oldManifold);

    void postSolve(Fixture self, Fixture other, Contact contact, ContactImpulse impulse);
}
