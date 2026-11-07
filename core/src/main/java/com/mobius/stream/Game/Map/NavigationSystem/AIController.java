package com.mobius.stream.Game.Map.NavigationSystem;


import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Entity.EntityActor;

public class AIController {
    private final EntityActor entityActor;
    private final NavGraph graph;
    private final FlowField flowField;
    private final float arriveThreshold = 1f;
    private final float launchThreshold = 5f;
    public int inputX = 0;
    public int inputY = 0;
    public float x, y;
    public boolean onGround = true;
    public boolean canDropThrough = false;
    private NavNode landingTarget = null;

    public AIController(EntityActor entityActor, NavGraph graph, FlowField flowField) {
        this.entityActor = entityActor;
        this.graph = graph;
        this.flowField = flowField;
    }

    public void update() {
        Vector2 position = entityActor.getPosition();
        x = position.x;
        y = position.y;
        onGround = entityActor.getWalker().isGrounded();
//        ai.canDropThrough = entitySystem.isOnDropThroughPlatform(ai);

        if (!onGround) {
            handleAirborne();
            return;
        }

        NavNode current = graph.findNearest(x, y);
        if (current == null) {
            inputX = 0;
            inputY = 0;
            return;
        }

        NavEdge edge = flowField.getNextEdge(current);
        if (edge == null) {
            // 到达目标或不可达
            inputX = 0;
            inputY = 0;
            return;
        }

        switch (edge.action()) {
            case WALK:
                executeWalk(edge);
                break;
            case JUMP:
                executeJump(edge);
                break;
            case FALL:
                executeFall(edge);
                break;
        }
    }

    /**
     * 空中：只控制水平，保持朝落点方向
     */
    private void handleAirborne() {
        if (landingTarget != null) {
            float dx = landingTarget.pos.x - x;
            inputX = Math.abs(dx) < arriveThreshold ? 0 : (int) Math.signum(dx);
        } else {
            inputX = 0;
        }
        inputY = 0;
    }

    private void executeWalk(NavEdge edge) {
        float dx = edge.to().pos.x - x;
        inputX = Math.abs(dx) < arriveThreshold ? 0 : (int) Math.signum(dx);
        inputY = 0;
    }

    private void executeJump(NavEdge edge) {
        float dxToLaunch = edge.from().pos.x - x;

        // 先走到起跳点
        if (Math.abs(dxToLaunch) > launchThreshold) {
            inputX = (int) Math.signum(dxToLaunch);
            inputY = 0;
            return;
        }

        // 起跳
        float dxToLand = edge.to().pos.x - x;
        inputX = Math.abs(dxToLand) < arriveThreshold ? 0 : (int) Math.signum(dxToLand);
        inputY = 1;
        landingTarget = edge.to();
        onGround = false;
    }

    private void executeFall(NavEdge edge) {
        float dxToLaunch = edge.from().pos.x - x;
        if (Math.abs(dxToLaunch) > launchThreshold) {
            inputX = (int) Math.signum(dxToLaunch);
            inputY = 0;
            return;
        }
        float dxToLand = edge.to().pos.x - x;
        if (Math.abs(dxToLand) >= arriveThreshold) {
            inputX = (int) Math.signum(dxToLand);
        } else {
            int outward = inferOutwardDir(edge.from());
            if (outward != 0) {
                inputX = outward;
            } else if (Math.abs(dxToLaunch) > 0.1F) {
                inputX = (int) Math.signum(dxToLaunch);
            } else {
                inputX = 0;
            }
        }

        if (canDropThrough) {
            inputY = -1;
            landingTarget = edge.to();
            onGround = false;
        } else {
            inputY = 0;
            landingTarget = edge.to();
        }

    }

    private int inferOutwardDir(NavNode edgeNode) {
        for (NavEdge navEdge : edgeNode.outgoing) {
            if (navEdge.action() == NavEdge.Action.WALK) {
                float dx = navEdge.to().pos.x - edgeNode.pos.x;
                if (Math.abs(dx) > 0.05F) {
                    return -((int) Math.signum(dx));
                }
            }
        }

        for (NavEdge navEdge : edgeNode.incoming) {
            if (navEdge.action() == NavEdge.Action.WALK) {
                float dx = navEdge.from().pos.x - edgeNode.pos.x;
                if (Math.abs(dx) > 0.05F) {
                    return -((int) Math.signum(dx));
                }
            }
        }

        return 0;
    }

    // ===== 物理系统回调 =====
    public void onLanded() {
        onGround = true;
        landingTarget = null;
    }

    public void onLeftGround() {
        onGround = false;
    }
}
