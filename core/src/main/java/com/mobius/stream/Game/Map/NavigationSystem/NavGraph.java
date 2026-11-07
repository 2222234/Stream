package com.mobius.stream.Game.Map.NavigationSystem;

import com.badlogic.gdx.utils.Array;

public class NavGraph {
    public final Array<NavNode> nodes = new Array<>();
    private int nextId = 0;

    public NavNode addNode(float x, float y) {
        NavNode n = new NavNode(nextId++, x, y);
        nodes.add(n);
        return n;
    }

    public NavEdge addEdge(NavNode from, NavNode to, float cost, NavEdge.Action action) {
        NavEdge e = new NavEdge(from, to, cost, action);
        from.outgoing.add(e);
        to.incoming.add(e);
        return e;
    }

    public NavNode findNearest(float x, float y) {
        NavNode best = null;
        float bestDx = Float.MAX_VALUE;
        float yTolerance = 1.5f; // 约 1.5 倍 AI 高度

        // 第一轮：只找高度接近的节点
        for (NavNode n : nodes) {
            float dy = Math.abs(n.pos.y - y);
            if (dy > yTolerance) continue;

            float dx = Math.abs(n.pos.x - x);
            if (dx < bestDx) {
                bestDx = dx;
                best = n;
            }
        }

        // 第二轮：如果同层没找到，退化为全局最近（容错）
        if (best == null) {
            float bestDst = Float.MAX_VALUE;
            for (NavNode n : nodes) {
                float d = n.pos.dst2(x, y);
                if (d < bestDst) {
                    bestDst = d;
                    best = n;
                }
            }
        }

        return best;
    }
}
