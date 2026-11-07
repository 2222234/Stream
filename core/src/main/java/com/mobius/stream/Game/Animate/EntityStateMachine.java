package com.mobius.stream.Game.Animate;

import com.mobius.stream.Game.Entity.EntityActor;
import com.mobius.stream.Game.Entity.EntityState;
import com.mobius.stream.Resource.Anim;

public class EntityStateMachine {
    private final StateLogic logic;
    private final AnimMap animMap;
    private final AnimationState animState;

    private EntityState current;

    public EntityStateMachine(StateLogic logic, AnimMap animMap,
                              AnimationState animState, EntityState initial) {
        this.logic = logic;
        this.animMap = animMap;
        this.animState = animState;
        this.current = initial;

        Anim initialAnim = animMap.get(initial, true);
        if (initialAnim != null) animState.play(initialAnim);
    }

    public void update(float delta, EntityActor self) {
        // 当前动画被锁定（一次性动作播放中）→ 不切换
        if (!animState.isLocked()) {
            EntityState next = logic.decide(delta, self, current);
            Anim target = animMap.get(next, self.isFacingRight());

            // ★ 关键：比较的是"目标动画"，不是"状态"
            if (target != null && target != animState.getCurrentAnim()) {
                current = next;
                animState.play(target);
            }
        }
        animState.update(delta);
    }

    public EntityState getCurrent() {
        return current;
    }

    public AnimationState getAnimState() {
        return animState;
    }
}
