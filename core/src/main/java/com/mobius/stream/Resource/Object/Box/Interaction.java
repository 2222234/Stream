package com.mobius.stream.Resource.Object.Box;

import com.mobius.stream.Game.Object.TipBox;

public enum Interaction {
    EarPhone1(EarPhone1.class), GunTable(GT.class), Map(Map.class),
    GoTo(GoTo.class),
    Pointer1(Pointer1.class), Pointer2(Pointer2.class), Pointer3(Pointer3.class), Clock(Clock.class),
    Hat1(Hat1.class), Hanger(Hanger.class),
    Meat(Meat.class), NaiEgg(NaiEgg.class), Window(Window.class), Computer(Computer.class);
    public final Class<? extends TipBox> tipBox;

    Interaction(Class<? extends TipBox> tipBox) {
        this.tipBox = tipBox;
    }
}
