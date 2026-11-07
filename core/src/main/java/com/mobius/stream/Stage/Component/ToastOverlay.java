package com.mobius.stream.Stage.Component;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.ResourcesRecord;

public class ToastOverlay extends WidgetGroup {
    private static final float LEFT_PADDING = 24f;

    private static final float MAIN_TOP_OFFSET = 24f;
    private static final float MAIN_SLOT_HEIGHT = 44f;

    private static final float FIRST_SUB_TOP_OFFSET = 200f;
    private static final float SUB_SLOT_HEIGHT = 32f;
    private static final float SUB_GAP = 4f;

    private static final int MAX_SUB = 10;

    private static final float CENTER_TOP_OFFSET = 500f;
    private static final float CENTER_SLOT_HEIGHT = 80f;

    private static final float UNDERLINE_PADDING = 2f;

    private static final float MAIN_FADE_IN = 1f;
    private static final float MAIN_FADE_OUT = 0.5f;
    private static final float SUB_SLIDE_IN = 0.35f;
    private static final float SUB_FADE_OUT = 0.30f;
    private static final float SUB_SHIFT = 0.20f;

    private final Skin skin;

    private final Table mainHolder = new Table();
    private final Table centerHolder = new Table();
    private final Group subList = new Group();

    private final float subListHeight =
        MAX_SUB * SUB_SLOT_HEIGHT + (MAX_SUB - 1) * SUB_GAP;

    private final Array<SubToast> subItems = new Array<>();
    private final Array<SubToast> fadeQueue = new Array<>();
    private boolean fadingOut = false;
    private SubToast currentExiting;

    public ToastOverlay(Resources resources) {
        skin = resources.getSkin();

        setFillParent(true);
        setTouchable(Touchable.disabled);

        mainHolder.top().left();

        addActor(mainHolder);
        addActor(subList);
        addActor(centerHolder);
    }

    @Override
    public void layout() {
        float w = getWidth();
        float h = getHeight();
        float contentW = Math.max(w - LEFT_PADDING * 2f, 0f);

        mainHolder.setBounds(
            LEFT_PADDING,
            h - MAIN_TOP_OFFSET - MAIN_SLOT_HEIGHT,
            contentW,
            MAIN_SLOT_HEIGHT);

        subList.setBounds(
            LEFT_PADDING,
            h - FIRST_SUB_TOP_OFFSET - subListHeight,
            contentW,
            subListHeight);

        centerHolder.setBounds(
            0f,
            h - CENTER_TOP_OFFSET - CENTER_SLOT_HEIGHT,
            w,
            CENTER_SLOT_HEIGHT);
    }

    public void showMain(CharSequence text, float holdSeconds) {
        mainHolder.clearChildren();

        Label label = new Label(text, skin, ResourcesRecord.Max_Font);
        label.setAlignment(Align.left);

        Table box = new Table();
        box.top().left();
        box.getColor().a = 0f;

        box.add(label).left().row();

        Image underline = new Image(skin.getDrawable(RegionRecord.UnderLine));
        box.add(underline)
            .left()
            .width(label.getPrefWidth())
            .padTop(UNDERLINE_PADDING);

        mainHolder.add(box).left().growY();

        box.addAction(Actions.sequence(
            Actions.alpha(1f, MAIN_FADE_IN, Interpolation.fade),
            Actions.delay(holdSeconds),
            Actions.fadeOut(MAIN_FADE_OUT, Interpolation.fade),
            Actions.run(() -> {
                if (box.getParent() != null) mainHolder.removeActor(box);
            })
        ));
    }

    public void showCenterTop(CharSequence text, float holdSeconds) {
        centerHolder.clearChildren();

        Label label = new Label(text, skin, ResourcesRecord.Mid_Font);
        label.setAlignment(Align.center);

        Table box = new Table();
        box.getColor().a = 0f;

        box.add(label).center().row();

        Image underline = new Image(skin.getDrawable(RegionRecord.UnderLine));
        box.add(underline)
            .center()
            .width(label.getPrefWidth())
            .padTop(UNDERLINE_PADDING);

        centerHolder.add(box).center();

        box.addAction(Actions.sequence(
            Actions.alpha(1f, MAIN_FADE_IN, Interpolation.fade),
            Actions.delay(holdSeconds),
            Actions.fadeOut(MAIN_FADE_OUT, Interpolation.fade),
            Actions.run(() -> {
                if (box.getParent() != null) centerHolder.removeActor(box);
            })
        ));
    }

    public void clearCenterTop() {
        centerHolder.clearChildren();
    }

    public void showSub(CharSequence text, float holdSeconds) {
        Label label = new Label(text, skin, ResourcesRecord.Mid_Font);
        label.setAlignment(Align.left);

        SlideHolder holder = new SlideHolder(label);
        SubToast item = new SubToast(holder, label, holdSeconds);

        subItems.add(item);
        subList.addActor(holder);

        layoutSubs(false);
        item.enter();

        while (subItems.size > MAX_SUB) {
            SubToast victim = null;
            for (int i = 0; i < subItems.size; i++) {
                SubToast s = subItems.get(i);
                if (s != currentExiting) {
                    victim = s;
                    break;
                }
            }
            if (victim == null) break;

            fadeQueue.removeValue(victim, true);
            removeSub(victim, false);
        }
    }

    public void clearAll() {
        mainHolder.clearChildren();
        centerHolder.clearChildren();
        fadeQueue.clear();
        fadingOut = false;
        currentExiting = null;
        subList.clearChildren();
        subItems.clear();
    }

    public int getSubCount() {
        return subItems.size;
    }

    private void layoutSubs(boolean animate) {
        for (int i = 0; i < subItems.size; i++) {
            SubToast s = subItems.get(i);
            float y = subListHeight - (i + 1) * SUB_SLOT_HEIGHT - i * SUB_GAP;

            s.holder.clearActions();
            if (animate) {
                s.holder.addAction(Actions.moveTo(
                    0f, y, SUB_SHIFT, Interpolation.pow2Out));
            } else {
                s.holder.setBounds(
                    0f, y, s.holder.getPrefWidth(), SUB_SLOT_HEIGHT);
            }
        }
    }

    private void removeSub(SubToast item, boolean animateShift) {
        if (item.removed && item.holder.getParent() == null) return;
        item.removed = true;

        item.label.clearActions();
        subItems.removeValue(item, true);
        if (item.holder.getParent() != null) item.holder.remove();

        layoutSubs(animateShift);

        if (item == currentExiting) {
            currentExiting = null;
            fadingOut = false;
            pumpQueue();
        }
    }

    private void requestFadeOut(SubToast item) {
        if (item.removed) return;
        if (!fadeQueue.contains(item, true)) fadeQueue.add(item);
        pumpQueue();
    }

    private void pumpQueue() {
        if (fadingOut) return;
        while (fadeQueue.size > 0) {
            SubToast item = fadeQueue.removeIndex(0);
            if (item.removed || item.holder.getParent() == null) continue;

            fadingOut = true;
            currentExiting = item;
            item.exit(() -> {
                fadingOut = false;
                currentExiting = null;
                pumpQueue();
            });
            return;
        }
    }

    public enum Type {
        Main, Sub, Center
    }

    static class SlideHolder extends Group {
        private final Label content;

        SlideHolder(Label content) {
            this.content = content;
            addActor(content);
            content.setSize(content.getPrefWidth(), content.getPrefHeight());
            setSize(content.getPrefWidth(), content.getPrefHeight());
        }

        public float getPrefWidth() {
            return content.getPrefWidth();
        }

        public float getPrefHeight() {
            return content.getPrefHeight();
        }

        @Override
        public void setBounds(float x, float y, float width, float height) {
            float keepX = content.getX();
            super.setBounds(x, y, width, height);
            content.setX(keepX);
            content.setY((height - content.getHeight()) / 2f);
        }
    }

    private class SubToast {
        final SlideHolder holder;
        final Label label;
        final float holdSeconds;
        boolean removed;

        SubToast(SlideHolder holder, Label label, float holdSeconds) {
            this.holder = holder;
            this.label = label;
            this.holdSeconds = holdSeconds;
        }

        void enter() {
            float startX = -holder.getPrefWidth() - 20f;
            label.setX(startX);
            label.getColor().a = 0f;

            label.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(0f, label.getY(),
                        SUB_SLIDE_IN, Interpolation.pow3Out),
                    Actions.alpha(1f, SUB_SLIDE_IN * 0.7f, Interpolation.fade)
                ),
                Actions.delay(holdSeconds),
                Actions.run(() -> requestFadeOut(this))
            ));
        }

        void exit(Runnable onDone) {
            label.clearActions();
            label.addAction(Actions.sequence(
                Actions.fadeOut(SUB_FADE_OUT, Interpolation.fade),
                Actions.run(() -> {
                    removeSub(this, true);
                    if (onDone != null) onDone.run();
                })
            ));
        }
    }
}
