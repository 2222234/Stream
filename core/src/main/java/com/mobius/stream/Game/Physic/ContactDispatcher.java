package com.mobius.stream.Game.Physic;

import com.badlogic.gdx.physics.box2d.*;

public class ContactDispatcher implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getBody().getUserData() instanceof ContactHandler handler) {
            handler.beginContact(a, b, contact);
        }
        if (b.getBody().getUserData() instanceof ContactHandler handler) {
            handler.beginContact(b, a, contact);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getBody().getUserData() instanceof ContactHandler handler) {
            handler.endContact(a, b, contact);
        }
        if (b.getBody().getUserData() instanceof ContactHandler handler) {
            handler.endContact(b, a, contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getBody().getUserData() instanceof ContactHandler handler) {
            handler.preSolve(a, b, contact, oldManifold);
        }
        if (b.getBody().getUserData() instanceof ContactHandler handler) {
            handler.preSolve(b, a, contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getBody().getUserData() instanceof ContactHandler handler) {
            handler.postSolve(a, b, contact, impulse);
        }
        if (b.getBody().getUserData() instanceof ContactHandler handler) {
            handler.postSolve(b, a, contact, impulse);
        }
    }
}
