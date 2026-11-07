package com.mobius.stream.Game.Animate;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mobius.stream.Resource.Anim;

public class AnimationState {
    private final AnimationCache cache;

    private Anim current;                          // 当前动画枚举
    private Animation<TextureRegion> animation;
    private float time;
    private boolean locked;

    public AnimationState(AnimationCache cache) {
        this.cache = cache;
    }

    public void play(Anim anim) {
        if (anim == null || anim == current) return;   // ← 同一动画不重播
        current = anim;
        animation = cache.get(anim);
        time = 0f;
        locked = anim.lockable;
    }

    public void update(float delta) {
        time += delta;
        if (locked
            && current.playMode == Animation.PlayMode.NORMAL
            && animation.isAnimationFinished(time)) {
            locked = false;
        }
    }

    public TextureRegion getFrame() {
        return animation == null ? null : animation.getKeyFrame(time);
    }

    /**
     * ★ 新增：给状态机比较目标动画用
     */
    public Anim getCurrentAnim() {
        return current;
    }

    public float getTime() {
        return time;
    }

    public boolean isLocked() {
        return locked;
    }
}
