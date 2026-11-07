package com.mobius.stream.Game.Map.NavigationSystem;


import com.badlogic.gdx.utils.Array;

import java.util.PriorityQueue;

public class FlowField {
    private final NavGraph graph;
    private final Array<NavNode> targets = new Array<>();

    public FlowField(NavGraph graph) {
        this.graph = graph;
    }

    public void setTargets(Array<NavNode> newTargets) {
        targets.clear();
        if (newTargets != null) targets.addAll(newTargets);
    }

    public void rebuild() {
        // 重置所有节点
        for (NavNode n : graph.nodes) {
            n.cost = Float.POSITIVE_INFINITY;
            n.nextEdge = null;
        }

        if (targets.isEmpty()) return;

        PriorityQueue<NavNode> open = new PriorityQueue<>(
            (a, b) -> Float.compare(a.cost, b.cost)
        );

        // 所有目标节点 cost = 0
        for (NavNode t : targets) {
            t.cost = 0;
            open.add(t);
        }

        // 沿 incoming 反向扩散
        while (!open.isEmpty()) {
            NavNode u = open.poll();

            for (NavEdge e : u.incoming) { // e.from -> u
                NavNode v = e.from();
                float newCost = u.cost + e.cost();

                if (newCost < v.cost) {
                    v.cost = newCost;
                    v.nextEdge = e; // v 走这条边即可到 u
                    open.add(v);
                }
            }
        }
    }

    public NavEdge getNextEdge(NavNode node) {
        return node == null ? null : node.nextEdge;
    }
}
