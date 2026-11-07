package com.mobius.stream.Game;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.PoolManager;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Map.BPS.BPSController;
import com.mobius.stream.Game.Map.NavigationSystem.Navigation;
import com.mobius.stream.Game.Physic.PhysicService;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Resource.Resources;

public record Context(Resources resources, PoolManager signalPool, Signal<GameEvent> signal,
                      PhysicService physicService, BPSController bpsController, Navigation navigation,
                      Stage stage, PlayerData playerData) {
}
