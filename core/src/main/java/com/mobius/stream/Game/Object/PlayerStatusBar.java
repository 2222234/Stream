package com.mobius.stream.Game.Object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Resource.RegionRecord;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.ResourcesRecord;
import com.mobius.stream.Resource.TextRecord;

public class PlayerStatusBar extends Table {
    private final Resources resources;
    private final PlayerData playerData;
    private final float GOLD_ROLLSPEED = 5f;
    private final Label goldLabel;
    private final Label hpLabel;
    private final BarActor hpBar;
    private float goldDisplay;
    private int goldTarget;
    private int lastSeenGold;
    private int lastHp = Integer.MIN_VALUE;

    public PlayerStatusBar(Resources resources, PlayerData playerData) {
        this.resources = resources;
        this.playerData = playerData;

        setFillParent(true);
        setVisible(false);
        bottom().pad(25f);

        Table leftBar = new Table() {
            @Override
            public void act(float delta) {
                super.act(delta);
                syncGold(delta);
                syncHp();
            }
        };
        leftBar.defaults().left();

        goldLabel = new Label("", resources.getSkin(), ResourcesRecord.Max_Font);
        leftBar.add(goldLabel).row();

        Label hpTag = new Label("HP", resources.getSkin(), ResourcesRecord.Max_Font);

        hpBar = new BarActor(resources.getAtlas().findRegion(RegionRecord.White), new Color(0f, 0f, 0f, 0.5f), new Color(0.75f, 0.25f, 0.25f, 1f));
        hpLabel = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);

        leftBar.add(hpTag).padRight(25f);
        leftBar.add(hpBar).size(500f, 40f).padRight(25f);
        leftBar.add(hpLabel);

        Table rightBar = new Table();
        rightBar.defaults().right();

        Weapon weaponWidget = new Weapon(resources);
        rightBar.add(weaponWidget).size(128f, 128f);

        add(leftBar).left().bottom().expandX();
        add(rightBar).right().bottom().expandX();
        initFromData();
    }

    private void initFromData() {
        int realGold = playerData.cost;
        goldDisplay = (float) realGold;
        goldTarget = realGold;
        lastSeenGold = realGold;
        goldLabel.setText(resources.getString(TextRecord.Text_Money) + realGold);

        syncHp();
    }

    private void syncGold(float delta) {
        int realGold = playerData.cost;
        if (realGold != lastSeenGold) {
            lastSeenGold = realGold;
            goldTarget = realGold;
        }

        if (goldDisplay != (float) goldTarget) {
            float t = 1f - (float) Math.exp(-GOLD_ROLLSPEED * delta);
            goldDisplay += ((float) goldTarget - goldDisplay) * t;
            if (Math.abs((float) goldTarget - goldDisplay) < 0.5F) {
                goldDisplay = (float) goldTarget;
            }

            goldLabel.setText(resources.getString(TextRecord.Text_Money) + Math.round(goldDisplay));
        }
    }

    private void syncHp() {
        int hp = (int) playerData.getHp();
        if (hp != lastHp) {
            lastHp = hp;
            hpLabel.setText(hp + " / " + (int) playerData.getMaxHp());
        }

        hpBar.setRatio(lastHp / playerData.getMaxHp());
    }

    public static class BarActor extends Actor {
        private final TextureRegion region;
        private final Color bgColor = new Color();
        private final Color fgColor = new Color();
        private float ratio = 1f;

        public BarActor(TextureRegion region, Color bg, Color fg) {
            this.region = region;
            bgColor.set(bg);
            fgColor.set(fg);
        }

        public void setRatio(float r) {
            ratio = MathUtils.clamp(r, 0f, 1f);
        }

        public void draw(Batch batch, float parentAlpha) {
            float a = parentAlpha * getColor().a;
            batch.setColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a * a);
            batch.draw(region, getX(), getY(), getWidth(), getHeight());
            batch.setColor(fgColor.r, fgColor.g, fgColor.b, fgColor.a * a);
            batch.draw(region, getX(), getY(), getWidth() * ratio, getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    private class Weapon extends Stack {
        Image weapon;
        Drawable gun1;
        Drawable gun2;

        Weapon(Resources resources) {
            weapon = new Image();
            gun1 = new TextureRegionDrawable(resources.getAtlas().findRegion(RegionRecord.Gun1));
            gun2 = new TextureRegionDrawable(resources.getAtlas().findRegion(RegionRecord.Gun2));
            add(weapon);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (playerData.getHolding() == 0) {
                weapon.setDrawable(null);
            }
            if (playerData.getHolding() == 1) {
                weapon.setDrawable(gun1);
            }
            if (playerData.getHolding() == 2) {
                weapon.setDrawable(gun2);
            }
        }
    }
}
