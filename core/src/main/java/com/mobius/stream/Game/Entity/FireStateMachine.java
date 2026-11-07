package com.mobius.stream.Game.Entity;

import com.mobius.stream.Game.Entity.Attribute.FireAttribute;

public class FireStateMachine {
    private final FireAttribute fireAttribute;
    private State state = State.Fire;

    private float fireTimer;
    private float stateTimer;

    public FireStateMachine(FireAttribute fireAttribute) {
        this.fireAttribute = fireAttribute;
    }

    public void update(float delta) {
        if (state == State.Fire && stateTimer > fireAttribute.fireTime()) {
            state = State.Cold;
        }
        if (state == State.Cold) {
            stateTimer += delta;
        }
        if (state == State.Cold && stateTimer > fireAttribute.fireTime() + fireAttribute.coldTime()) {
            state = State.Fire;
            stateTimer = 0;
        }
    }

    public boolean fire(boolean fire, float delta) {
        fireTimer += delta;
        if (state == State.Fire) {
            if (fire) {
                stateTimer += delta;
            } else {
                stateTimer -= delta;
            }
        }
        if (fire) {
            if (state == State.Fire && fireTimer >= fireAttribute.duration()) {
                fireTimer = 0;
                return true;
            }
        }
        return false;
    }

    public void reset() {
        state = State.Fire;
        fireTimer = 0;
        stateTimer = 0;
    }

    enum State {
        Fire, Cold
    }
}
