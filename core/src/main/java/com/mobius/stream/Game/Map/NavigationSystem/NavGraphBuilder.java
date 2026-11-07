package com.mobius.stream.Game.Map.NavigationSystem;

import com.badlogic.gdx.utils.Array;

public class NavGraphBuilder {

    // ========== 数据 ==========
    private final Array<Platform> platforms = new Array<>();
    // ========== 物理参数（必须和实体真实物理一致）==========
    public float jumpVelocity = 12f;   // 起跳初速度
    public float gravity = 30f;   // 重力加速度
    public float moveSpeed = 6f;    // 水平最大速度
    public float maxFallDistance = 5f; // 允许直接下落的最大高度

    // ========== 几何参数 ==========
    /**
     * 节点水平间距。默认 = moveSpeed * 0.5s
     */
    public float nodeSpacing = -1f;
    /**
     * 节点去重容差
     */
    public float mergeTolerance = 0.1f;

    // ========== 添加平台 ==========
    public NavGraphBuilder addPlatform(float x, float y, float width) {
        Platform p = new Platform(x, y, width);
        platforms.add(p);
        return this;
    }

    public NavGraphBuilder addPlatform(Platform p) {
        platforms.add(p);
        return this;
    }

    // ========== 构建 ==========
    public NavGraph build() {
        NavGraph graph = new NavGraph();

        float spacing = (nodeSpacing > 0f) ? nodeSpacing : (moveSpeed * 0.5f);

        // 1) 每个平台按 spacing 拆节点 + WALK 边
        Array<Array<NavNode>> platformNodes = new Array<>();
        for (Platform p : platforms) {
            Array<NavNode> nodes = buildPlatformNodes(graph, p, spacing);
            platformNodes.add(nodes);
        }

        // 2) 跨平台生成 JUMP / FALL 边
        for (int i = 0; i < platforms.size; i++) {
            for (int j = 0; j < platforms.size; j++) {
                if (i == j) continue;
                connectPlatforms(
                    graph,
                    platforms.get(i), platformNodes.get(i),
                    platforms.get(j), platformNodes.get(j)
                );
            }
        }

        return graph;
    }

    // ========== 单个平台生成节点 ==========
    private Array<NavNode> buildPlatformNodes(NavGraph graph, Platform p, float spacing) {
        Array<NavNode> nodes = new Array<>();

        int segments = Math.max(1, (int) Math.ceil(p.width / spacing));
        float segW = p.width / segments;

        NavNode prev = null;
        for (int i = 0; i <= segments; i++) {
            float nx = p.x + i * segW;
            NavNode node = getOrCreateNode(graph, nx, p.y);

            // 同平台相邻节点连 WALK
            if (prev != null && prev != node) {
                addWalkBoth(graph, prev, node);
            }
            // 去重后可能返回已有节点，避免重复加入列表
            if (!nodes.contains(node, true)) {
                nodes.add(node);
            }
            prev = node;
        }
        return nodes;
    }

    // ========== 节点去重 ==========
    private NavNode getOrCreateNode(NavGraph graph, float x, float y) {
        for (NavNode n : graph.nodes) {
            if (Math.abs(n.pos.x - x) < mergeTolerance
                && Math.abs(n.pos.y - y) < mergeTolerance) {
                return n;
            }
        }
        return graph.addNode(x, y);
    }

    // ========== 跨平台连接 ==========
    private void connectPlatforms(NavGraph graph,
                                  Platform fromP, Array<NavNode> fromNodes,
                                  Platform toP, Array<NavNode> toNodes) {

        float dy = toP.y - fromP.y;

        // 起跳/下落点只用平台边缘节点（首尾）
        NavNode fromLeft = fromNodes.first();
        NavNode fromRight = fromNodes.peek();
        NavNode[] launchNodes = (fromLeft == fromRight)
            ? new NavNode[]{fromLeft}
            : new NavNode[]{fromLeft, fromRight};

        for (NavNode fn : launchNodes) {
            for (NavNode tn : toNodes) {
                if (fn == tn) continue;

                float dx = tn.pos.x - fn.pos.x;
                float adx = Math.abs(dx);

                if (dy > 0.5f) {
                    // 向上跳
                    if (canJumpUp(dy, adx)) {
                        float cost = 8f + adx + dy * 1.5f;
                        addEdgeIfAbsent(graph, fn, tn, cost, NavEdge.Action.JUMP);
                    }
                } else if (dy < -0.5f) {
                    // 下落
                    float drop = -dy;
                    if (drop > maxFallDistance) continue;
                    if (canFall(drop, adx)) {
                        float cost = 4f + adx + drop * 0.2f;
                        if (toP.passThrough) cost -= 1.5f;
                        addEdgeIfAbsent(graph, fn, tn, cost, NavEdge.Action.FALL);
                    }
                }
            }
        }
    }

    // ========== 边工具 ==========
    private void addWalkBoth(NavGraph graph, NavNode a, NavNode b) {
        float cost = a.pos.dst(b.pos);
        addEdgeIfAbsent(graph, a, b, cost, NavEdge.Action.WALK);
        addEdgeIfAbsent(graph, b, a, cost, NavEdge.Action.WALK);
    }

    /**
     * 避免重复加同一条边
     */
    private void addEdgeIfAbsent(NavGraph graph, NavNode from, NavNode to,
                                 float cost, NavEdge.Action action) {
        for (NavEdge e : from.outgoing) {
            if (e.to() == to && e.action() == action) return;
        }
        graph.addEdge(from, to, cost, action);
    }

    // ========== 跳跃/下落可达性 ==========
    private boolean canJumpUp(float dy, float dx) {
        float maxHeight = (jumpVelocity * jumpVelocity) / (2f * gravity);
        if (dy > maxHeight) return false;

        float disc = jumpVelocity * jumpVelocity - 2f * gravity * dy;
        if (disc < 0) return false;
        float sqrtDisc = (float) Math.sqrt(disc);
        float t2 = (jumpVelocity + sqrtDisc) / gravity; // 下落途中经过 dy 的最晚时间
        float maxDx = moveSpeed * t2;
        return dx <= maxDx;
    }

    private boolean canFall(float drop, float dx) {
        float t = (float) Math.sqrt(2f * drop / gravity);
        float maxDx = moveSpeed * t;
        return dx <= maxDx;
    }

    // ========== 平台定义 ==========
    public static class Platform {
        public float x, y, width;
        public boolean passThrough = false; // 是否可下穿

        public Platform() {
        }

        public Platform(float x, float y, float width) {
            this.x = x;
            this.y = y;
            this.width = width;
        }
    }
}
