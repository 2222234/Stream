package com.mobius.stream.Game.Animate;

import com.mobius.stream.Game.Entity.EntityState;
import com.mobius.stream.Resource.Anim;

import java.util.EnumMap;

public class AnimMap {
    private final EnumMap<EntityState, Anim> right = new EnumMap<>(EntityState.class);
    private final EnumMap<EntityState, Anim> left = new EnumMap<>(EntityState.class);

    /**
     * 无方向区分的状态
     */
    public AnimMap put(EntityState s, Anim anim) {
        right.put(s, anim);
        left.put(s, anim);
        return this;
    }

    /**
     * 有方向区分的状态（例如 MOVE）
     */
    public AnimMap put(EntityState s, Anim rightAnim, Anim leftAnim) {
        right.put(s, rightAnim);
        left.put(s, leftAnim);
        return this;
    }

    public Anim get(EntityState s, boolean facingRight) {
        return (facingRight ? right : left).get(s);
    }
}
