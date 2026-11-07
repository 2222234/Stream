package com.mobius.stream.Game.Map.NavigationSystem;


import com.badlogic.gdx.utils.Array;

public class FlowFieldManager {
    private final NavGraph graph;
    private final FlowField flowField;

    private final Array<NavNode> pendingTargets = new Array<>();
    private final float rebuildInterval = 0.3f; // 每 0.3 秒重建一次
    private boolean dirty = false;
    private float timer = 0f;

    public FlowFieldManager(NavGraph graph) {
        this.graph = graph;
        this.flowField = new FlowField(graph);
    }

    public void setTarget(float x, float y) {
        NavNode n = graph.findNearest(x, y);
        if (n == null) return;
        pendingTargets.clear();
        pendingTargets.add(n);
        dirty = true;
    }

    public void setTargets(Array<NavNode> nodes) {
        pendingTargets.clear();
        pendingTargets.addAll(nodes);
        dirty = true;
    }

    /**
     * 立即重建（初始化时调用一次）
     */
    public void forceRebuild() {
        flowField.setTargets(pendingTargets);
        flowField.rebuild();
        dirty = false;
        timer = 0f;
    }

    public void update(float dt) {
        timer += dt;
        if (dirty && timer >= rebuildInterval) {
            forceRebuild();
        }
    }

    public FlowField getFlowField() {
        return flowField;
    }
}
