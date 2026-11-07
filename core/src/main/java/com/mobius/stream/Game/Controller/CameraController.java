package com.mobius.stream.Game.Controller;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mobius.stream.Game.Entity.Interface.Controllable;
import com.mobius.stream.Game.Event.Game.Camera.FocusCameraEvent;
import com.mobius.stream.Game.Event.Game.Camera.LockCameraEvent;
import com.mobius.stream.Game.Event.Game.Camera.ZoomCameraEvent;
import com.mobius.stream.Game.Event.Game.Map.MapUpdated;
import com.mobius.stream.Game.Event.GameEvent;

public class CameraController implements Listener<GameEvent> {
    private static final float CAMERA_SPEED = 0.1f;
    private static final float ZOOM_SPEED = 0.2f;

    private final OrthographicCamera camera;
    private final Vector2 target = new Vector2();
    private final Rectangle bounds = new Rectangle();
    private final Vector2 mouseUV = new Vector2();
    private float targetZoom;
    private float ratio;

    private Controllable controllable;
    private FocusType focusType = FocusType.Lock;
    private boolean hasBounds = false;

    private boolean lock;

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    private void focusOn(Controllable controllable, float ratio) {
        this.controllable = controllable;
        this.ratio = ratio;
        focusType = FocusType.Focus;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    private void lockOn(Vector2 target) {
        this.target.set(target);
        focusType = FocusType.Lock;
    }

    private void zoom(float targetZoom) {
        this.targetZoom = targetZoom;
    }

    private void setBounds(Rectangle bounds, boolean hasBounds) {
        this.bounds.set(bounds);
        this.hasBounds = hasBounds;
    }

    @Override
    public void receive(Signal<GameEvent> signal, GameEvent object) {
        if (object instanceof FocusCameraEvent focusCameraEvent) {
            focusOn(focusCameraEvent.controllable, focusCameraEvent.ratio);
            return;
        }
        if (object instanceof LockCameraEvent lockCameraEvent) {
            lockOn(lockCameraEvent.target);
            return;
        }
        if (object instanceof ZoomCameraEvent zoomCameraEvent) {
            zoom(zoomCameraEvent.zoom);
            return;
        }
        if (object instanceof MapUpdated mapUpdated) {
            setBounds(mapUpdated.bounds, mapUpdated.hasBounds);
        }
    }

    public void update(float delta) {
        if (focusType == FocusType.Focus) {
            if (!lock) {
                if (controllable != null) {
                    float width = camera.viewportWidth * camera.zoom / 2;
                    float height = camera.viewportHeight * camera.zoom / 2;
                    updateMouseUV();
                    mouseUV.scl(width, height).scl(ratio).add(controllable.getPosition());
                    target.set(mouseUV);
                    clampToBounds(target);
                }
            }
        }

        float newCamX = MathUtils.lerp(camera.position.x, target.x, CAMERA_SPEED);
        float newCamY = MathUtils.lerp(camera.position.y, target.y, CAMERA_SPEED);
        camera.position.set(newCamX, newCamY, 0);

        float newZoom = MathUtils.lerp(camera.zoom, targetZoom, ZOOM_SPEED);
        camera.zoom = clampZoom(MathUtils.clamp(newZoom, 0.01f, 10f));
        camera.update();
    }

    private void clampToBounds(Vector2 pos) {
        if (!hasBounds) return;
        float halfW = camera.viewportWidth * camera.zoom * 0.5f;
        float halfH = camera.viewportHeight * camera.zoom * 0.5f;
        pos.x = MathUtils.clamp(pos.x, bounds.x + halfW, bounds.x + bounds.width - halfW);
        pos.y = MathUtils.clamp(pos.y, bounds.y + halfH, bounds.y + bounds.height - halfH);
    }

    private float clampZoom(float zoom) {
        if (!hasBounds) return zoom;
        return MathUtils.clamp(zoom, 0, Math.min(
            bounds.width / camera.viewportWidth,
            bounds.height / camera.viewportHeight)
        );
    }

    private void updateMouseUV() {
        float x = Gdx.input.getX();
        float y = Gdx.input.getY();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        float u = (x - w / 2f) / (w / 2f);
        float v = (h / 2f - y) / (h / 2f);
        mouseUV.set(u, v);
    }

    enum FocusType {
        Lock,
        Focus,
        Animation
    }
}
