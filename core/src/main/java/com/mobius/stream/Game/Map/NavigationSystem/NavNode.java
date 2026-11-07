package com.mobius.stream.Game.Map.NavigationSystem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NavNode {
    public final int id;
    public final Vector2 pos;
    public final Array<NavEdge> outgoing = new Array<>();
    public final Array<NavEdge> incoming = new Array<>();

    // 流场缓存（由 FlowField 填充）
    public float cost = Float.POSITIVE_INFINITY;
    public NavEdge nextEdge = null;

    public NavNode(int id, float x, float y) {
        this.id = id;
        this.pos = new Vector2(x, y);
    }
}
