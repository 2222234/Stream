package com.mobius.stream.Game.Object;

public interface Interact {
    void interact(Type type);

    enum Type {
        Use, Back
    }
}
