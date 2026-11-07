package com.mobius.stream.Game.Map.NavigationSystem;

public record NavEdge(NavNode from, NavNode to, float cost, Action action) {

    public enum Action {WALK, JUMP, FALL}
}
