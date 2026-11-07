package com.mobius.stream.Game.Controller;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.IntSet;
import com.mobius.stream.Game.Entity.Interface.Controllable;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.Game.Control.FocusControlEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Ui.ClearTipBoxEvent;
import com.mobius.stream.Game.Event.Ui.TipEvent;
import com.mobius.stream.Game.Object.Interact;
import com.mobius.stream.Game.Object.TipBox;
import com.mobius.stream.Resource.AudioRecord;

public class ControlController extends InputAdapter implements Listener<GameEvent> {
    private final Stage stage;
    private final Signal<GameEvent> signal;
    private final Vector2 temp = new Vector2();
    private final Vector2 input = new Vector2();
    private final IntSet inputKeys = new IntSet();
    private final IntSet inputButtons = new IntSet();
    private boolean interact = false;
    private boolean fire = false;
    private int flag = 0;
    private Controllable controllable;
    private TipBox tipBox;
    private boolean lock;

    public ControlController(Stage stage, Signal<GameEvent> signal) {
        this.stage = stage;
        this.signal = signal;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public boolean keyDown(int keycode) {
        inputKeys.add(keycode);
        if (keycode == Input.Keys.E) interact = true;
        if (keycode == Input.Keys.UP) flag = 1;
        if (keycode == Input.Keys.DOWN) flag = -1;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        inputKeys.remove(keycode);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        flag = (int) Math.signum(amountY);
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputButtons.add(button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputButtons.remove(button);
        return true;
    }

    private void check() {
        if (inputKeys.contains(Input.Keys.A)) input.x = -1;
        if (inputKeys.contains(Input.Keys.D)) input.x = 1;
        if (inputKeys.contains(Input.Keys.W)) input.y = 1;
        if (inputKeys.contains(Input.Keys.S)) input.y = -1;
        if (inputButtons.contains(Input.Buttons.LEFT)) fire = true;
    }

    public void update(float delta) {
        check();
        if (!lock) {
            if (controllable != null) {
                controllable.setMoveInput(input);
                if (!input.isZero()) {
                    signal.dispatch(new PlaySoundEvent(AudioRecord.FootStep, 0.5f, 0.5f, 0));
                }
                controllable.setHold(flag);
                temp.set(Gdx.input.getX(), Gdx.input.getY());
                stage.getViewport().unproject(temp);
                temp.sub(controllable.getPosition());
                controllable.setTargetInput(temp.angleRad());
                if (fire) controllable.fire();
            }
            if (tipBox != null) if (interact) tipBox.interact(Interact.Type.Use);
        }
        reset();
    }

    private void reset() {
        input.setZero();
        interact = false;
        fire = false;
        flag = 0;
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof FocusControlEvent focusControlEvent) {
            controllable = focusControlEvent.controllable;
            return;
        }
        if (object instanceof TipEvent tipEvent) {
            if (tipEvent.visible) tipBox = tipEvent.tipBox;
            return;
        }
        if (object instanceof ClearTipBoxEvent) {
            tipBox = null;
        }
    }
}
