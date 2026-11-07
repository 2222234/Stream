package com.mobius.stream.Game.Entity.Attribute;

public enum NameType {
    Dude(0),
    Ticket(50), Train(100), Clock(5000),
    Stair(100), Pos(200), Hat(10000),
    Can(200), Tray(400), Cook(20000),
    Computer(50000);
    public final int cost;

    NameType(int cost) {
        this.cost = cost;
    }
}
