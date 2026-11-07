package com.mobius.stream.Game.Map.NavigationSystem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NavDebugRenderer {

    private final ShapeRenderer sr = new ShapeRenderer();

    // 可调参数
    public float nodeRadius = 0.25f;
    public float edgeThickness = 2f;      // 线宽（像素）
    public float flowThickness = 4f;      // 流场线更粗
    public float aiSize = 0.5f;

    // 颜色配置
    public Color nodeColor = new Color(1f, 1f, 1f, 1f);
    public Color walkColor = new Color(0.2f, 1f, 0.2f, 0.6f);  // 绿
    public Color jumpColor = new Color(1f, 0.9f, 0.2f, 0.8f);  // 黄
    public Color fallColor = new Color(1f, 0.4f, 0.1f, 0.8f);  // 橙
    public Color flowColor = new Color(0.2f, 0.6f, 1f, 1f);    // 蓝
    public Color targetColor = new Color(1f, 0.1f, 0.1f, 1f);    // 红
    public Color aiColor = new Color(0.8f, 0.2f, 1f, 1f);    // 紫
    public Color jumpTextColor = new Color(1f, 1f, 1f, 1f);

    public boolean showAllEdges = true;   // 显示所有导航边
    public boolean showFlow = true;       // 显示流场方向
    public boolean showNodes = true;
    public boolean showAIs = true;
    public boolean showTargets = true;

    public void render(OrthographicCamera camera, NavGraph graph,
                       FlowFieldManager flowManager,
                       Array<AIController> ais,
                       Array<NavNode> targets) {

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        if (showAllEdges) drawEdges(graph);
        if (showFlow) drawFlowField(graph);
        if (showNodes) drawNodes(graph);
        if (showTargets && targets != null) drawTargets(targets);
        if (showAIs) drawAIs(ais);

        sr.end();
    }

    // ========== 绘制所有导航边 ==========
    private void drawEdges(NavGraph graph) {
        sr.setColor(walkColor);
        for (NavNode n : graph.nodes) {
            for (NavEdge e : n.outgoing) {
                if (e.action() == NavEdge.Action.WALK) {
                    drawLine(e.from().pos, e.to().pos, walkColor);
                }
            }
        }

        for (NavNode n : graph.nodes) {
            for (NavEdge e : n.outgoing) {
                if (e.action() == NavEdge.Action.JUMP) {
                    drawLine(e.from().pos, e.to().pos, jumpColor);
                }
            }
        }

        for (NavNode n : graph.nodes) {
            for (NavEdge e : n.outgoing) {
                if (e.action() == NavEdge.Action.FALL) {
                    drawLine(e.from().pos, e.to().pos, fallColor);
                }
            }
        }
    }

    // ========== 绘制流场：每个节点画一条指向 nextEdge.to 的箭头 ==========
    private void drawFlowField(NavGraph graph) {
        for (NavNode n : graph.nodes) {
            if (n.nextEdge == null) continue;

            Vector2 from = n.pos;
            Vector2 to = n.nextEdge.to().pos;

            // 主线
            drawLine(from, to, flowColor, flowThickness);

            // 箭头
            drawArrowHead(from, to, flowColor);
        }
    }

    private void drawArrowHead(Vector2 from, Vector2 to, Color color) {
        Vector2 dir = new Vector2(to).sub(from).nor();
        Vector2 mid = new Vector2(from).lerp(to, 0.85f);

        float headLen = 0.5f;
        float headWidth = 0.35f;
        Vector2 perp = new Vector2(-dir.y, dir.x).scl(headWidth);

        Vector2 p1 = new Vector2(mid).mulAdd(dir, headLen);
        Vector2 p2 = new Vector2(mid).add(perp);
        Vector2 p3 = new Vector2(mid).sub(perp);

        sr.setColor(color);
        sr.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }

    // ========== 绘制节点 ==========
    private void drawNodes(NavGraph graph) {
        sr.setColor(nodeColor);
        for (NavNode n : graph.nodes) {
            sr.circle(n.pos.x, n.pos.y, nodeRadius, 12);
        }

        // 有流场的节点用不同颜色高亮
        for (NavNode n : graph.nodes) {
            if (n.nextEdge != null) {
                sr.setColor(0.3f, 0.8f, 1f, 1f);
                sr.circle(n.pos.x, n.pos.y, nodeRadius * 0.5f, 10);
            }
        }
    }

    // ========== 目标点 ==========
    private void drawTargets(Array<NavNode> targets) {
        sr.setColor(targetColor);
        for (NavNode t : targets) {
            sr.circle(t.pos.x, t.pos.y, nodeRadius * 2f, 16);
            sr.circle(t.pos.x, t.pos.y, nodeRadius * 1.2f, 16);
        }
    }

    // ========== AI ==========
    private void drawAIs(Array<AIController> ais) {
        for (AIController ai : ais) {
            sr.setColor(aiColor);
            sr.rect(ai.x - aiSize * 0.5f, ai.y - aiSize * 0.5f, aiSize, aiSize);

            // 输入指示：小箭头表示移动方向
            if (ai.inputX != 0) {
                float ax = ai.x + ai.inputX * 0.8f;
                sr.setColor(0f, 1f, 0f, 1f);
                sr.rectLine(ai.x, ai.y + aiSize, ax, ai.y + aiSize, 2f);
            }
            if (ai.inputY == 1) {
                sr.setColor(1f, 1f, 0f, 1f);
                sr.circle(ai.x, ai.y + aiSize * 1.5f, 0.15f, 8);
            } else if (ai.inputY == -1) {
                sr.setColor(1f, 0.5f, 0f, 1f);
                sr.circle(ai.x, ai.y - aiSize * 1.5f, 0.15f, 8);
            }
        }
    }

    // ========== 工具方法 ==========
    private void drawLine(Vector2 a, Vector2 b, Color color) {
        drawLine(a, b, color, edgeThickness);
    }

    private void drawLine(Vector2 a, Vector2 b, Color color, float thickness) {
        sr.setColor(color);
        sr.rectLine(a.x, a.y, b.x, b.y, thickness * 0.02f); // 世界单位宽度
    }

    public void dispose() {
        sr.dispose();
    }
}
