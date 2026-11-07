package com.mobius.stream.Game;

import com.badlogic.gdx.utils.Array;

/**
 * 基于游戏时间的时间轴调度器（delta 驱动，可与暂停 / 时间缩放联动）。
 * <p>
 * 用法：
 * Timeline tl = new Timeline()
 * .after(1f,  t -> spawnEnemy())
 * .after(0.5f, t -> spawnEnemy())
 * .at(3f,    t -> spawnBoss())
 * .loop(true);
 * <p>
 * // render(float delta)
 * tl.update(delta);
 */
public class Timeline {

    private static final int MAX_CYCLES_PER_UPDATE = 1000;
    // 事件与时间信息
    private final Array<Event> events = new Array<>();
    private int orderCounter = 0;
    private float duration = 0f;   // 最大事件时间（一个周期长度）
    private float cursor = 0f;   // after() 相对时间游标
    // 运行时状态
    private float elapsed = 0f;
    private float timeScale = 1f;
    private boolean looping = false;
    private int loopCount = 0;     // -1 表示无限
    private int loopsDone = 0;
    private boolean paused = false;
    private boolean finished = false;
    private Action onLoop;
    private Action onComplete;
    // 内部索引（增量触发优化）
    private boolean structureDirty = true;
    private int nextIndex = 0;

    /**
     * 在绝对时间 time（秒）触发一次
     */
    public Timeline at(float time, Action action) {
        Event e = new Event();
        e.time = Math.max(0f, time);
        e.order = orderCounter++;
        e.action = action;
        events.add(e);
        structureDirty = true;
        if (e.time > duration) duration = e.time;
        return this;
    }

    public Timeline at(float time, Runnable action) {
        return at(time, t -> action.run());
    }

    // ============================ 构建 API ============================

    /**
     * 相对上一个 after/at 的时间点延迟 delay 秒触发（适合写脚本式流程）
     */
    public Timeline after(float delay, Action action) {
        cursor += Math.max(0f, delay);
        return at(cursor, action);
    }

    public Timeline after(float delay, Runnable action) {
        return after(delay, t -> action.run());
    }

    /**
     * 从 start 开始，每隔 interval 秒触发一次，共 count 次
     */
    public Timeline every(float start, float interval, int count, Action action) {
        if (interval <= 0f) throw new IllegalArgumentException("interval must be > 0");
        if (count < 0) throw new IllegalArgumentException("count must be >= 0");
        for (int i = 0; i < count; i++) {
            at(start + i * interval, action);
        }
        return this;
    }

    public Timeline every(float start, float interval, int count, Runnable action) {
        return every(start, interval, count, t -> action.run());
    }

    /**
     * 是否无限循环
     */
    public Timeline loop(boolean looping) {
        this.looping = looping;
        if (!looping) this.loopCount = 0;
        return this;
    }

    /**
     * 总共播放 loopCount 次（含第一次）；loopCount = -1 表示无限循环
     */
    public Timeline loop(int loopCount) {
        this.looping = true;
        this.loopCount = loopCount;
        return this;
    }

    // ============================ 控制 API ============================

    public Timeline onLoop(Action a) {
        this.onLoop = a;
        return this;
    }

    public Timeline onLoop(Runnable a) {
        return onLoop(t -> a.run());
    }

    public Timeline onComplete(Action a) {
        this.onComplete = a;
        return this;
    }

    public Timeline onComplete(Runnable a) {
        return onComplete(t -> a.run());
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void togglePause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isLooping() {
        return looping;
    }

    public float getElapsed() {
        return elapsed;
    }

    public float getDuration() {
        return duration;
    }

    public float getTimeScale() {
        return timeScale;
    }

    public Timeline setTimeScale(float scale) {
        this.timeScale = scale;
        return this;
    }

    public int getLoopsDone() {
        return loopsDone;
    }

    /**
     * 当前周期进度 [0,1]
     */
    public float getProgress() {
        if (duration <= 0f) return finished ? 1f : 0f;
        return Math.min(1f, elapsed / duration);
    }

    /**
     * 从头开始（保留事件定义）
     */
    public void reset() {
        elapsed = 0f;
        loopsDone = 0;
        finished = false;
        for (int i = 0; i < events.size; i++) events.get(i).fired = false;
        structureDirty = true;
    }

    /**
     * 清空全部（事件 + 回调 + 状态）
     */
    public void clear() {
        events.clear();
        orderCounter = 0;
        duration = 0f;
        cursor = 0f;
        elapsed = 0f;
        loopsDone = 0;
        finished = false;
        paused = false;
        nextIndex = 0;
        structureDirty = true;
    }

    /**
     * 立刻终止（不再触发任何事件，也不会触发 onComplete）
     */
    public void kill() {
        finished = true;
    }

    public void update(float delta) {
        if (finished || paused || delta <= 0f) return;

        elapsed += delta * timeScale;

        int cycles = 0;
        while (true) {
            fireDueEvents();

            // 没到周期终点，或时间轴没有时长，退出
            if (duration <= 0f || elapsed < duration) break;

            // 防止极端情况死循环（一帧跨了非常多周期）
            if (++cycles > MAX_CYCLES_PER_UPDATE) {
                elapsed = duration;
                break;
            }

            boolean canLoop = looping && (loopCount < 0 || loopsDone < loopCount - 1);
            if (canLoop) {
                loopsDone++;
                elapsed -= duration;   // 用减法保留超出的部分，避免时间漂移
                restartCycle();
                if (onLoop != null) onLoop.run(this);
                if (finished) break;   // 回调里 kill() 了
            } else {
                finished = true;
                if (onComplete != null) onComplete.run(this);
                break;
            }
        }
    }

    private void restartCycle() {
        for (int i = 0; i < events.size; i++) events.get(i).fired = false;
        structureDirty = true;
    }

    // ============================ 驱动 ============================

    private void fireDueEvents() {
        // 惰性排序：只有新增了事件（或重置）才 sort
        if (structureDirty) {
            events.sort();
            structureDirty = false;
            nextIndex = 0;
        }
        // 增量触发：正常情况下每帧 O(1)
        while (nextIndex < events.size) {
            Event e = events.get(nextIndex);
            if (elapsed < e.time) break;
            nextIndex++;
            if (e.fired) continue;
            e.fired = true;
            if (e.action != null) e.action.run(this);
        }
    }

    /**
     * 时间轴动作，回调里可以拿到 Timeline 本身（比如在回调里 kill 自己）
     */
    public interface Action {
        void run(Timeline timeline);
    }

    private static class Event implements Comparable<Event> {
        float time;
        int order;          // 同一时刻保持插入顺序
        Action action;
        boolean fired;

        @Override
        public int compareTo(Event o) {
            int c = Float.compare(time, o.time);
            return c != 0 ? c : Integer.compare(order, o.order);
        }
    }
}
