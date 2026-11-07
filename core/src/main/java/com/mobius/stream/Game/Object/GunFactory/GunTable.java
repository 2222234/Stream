package com.mobius.stream.Game.Object.GunFactory;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Null;
import com.mobius.stream.Game.Event.Game.Audio.PlaySoundEvent;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Game.Event.Ui.ShowTitleEvent;
import com.mobius.stream.Resource.*;
import com.mobius.stream.Resource.Object.Bullet.BulletEnum;
import com.mobius.stream.Resource.Player.PlayerData;
import com.mobius.stream.Stage.Component.ToastOverlay;
import com.mobius.stream.Stage.Element.UnderLineButton;

import java.lang.reflect.InvocationTargetException;

public class GunTable extends Table {
    private final Resources resources;
    private final Signal<GameEvent> signal;
    private final PlayerData playerData;

    private final float SIZE = 128f;
    private final DragAndDrop dragAndDrop;
    private ChainContext main;
    private ChainContext focusing;
    private Table chainTable;
    private Table bulletTable;
    private Group currentLayout;
    private boolean target1 = true;

    private BulletEnum select;
    private String src = "";

    public GunTable(Resources resources, Signal<GameEvent> signal, PlayerData playerData) {
        this.resources = resources;
        this.signal = signal;
        this.playerData = playerData;
        setFillParent(true);
        setVisible(false);
        setBackground(new TextureRegionDrawable(resources.getAtlas().findRegion(RegionRecord.White)).tint(new Color(0.5f, 0.5f, 0.5f, 0.5f)));

        dragAndDrop = new DragAndDrop();
        dragAndDrop.setDragActorPosition(SIZE / 2f, -SIZE / 2f);

        buildChainTable();
        buildInformationTable();
        buildBulletTable();
    }

    private void buildChainTable() {
        chainTable = new Table();
        chainTable.setFillParent(true);
        add(chainTable).fillX().expandX().expandY().bottom().row();

        switchLayout(null);
    }

    private void switchLayout(BulletEnum bullet) {
        chainTable.clearChildren();

        if (bullet == null) {
            currentLayout = buildSingleCentered();
        } else {
            switch (bullet) {
                case Bomb, Splitter:
                    currentLayout = buildScatter();
                    break;
                case Dropper, Emitter:
                    currentLayout = buildHorizontalRow();
                    break;
            }
        }

        chainTable.add(currentLayout)
            .size(currentLayout.getWidth(), currentLayout.getHeight())
            .expand()
            .center();
    }

    private Group buildSingleCentered() {
        Table t = new Table();
        ChainBox s = createChainBox(-1);
        if (main != null) {
            s.setImage(main.bullet.bulletEnum);
        }
        t.add(s).size(SIZE, SIZE).center();
        return t;
    }

    private Group buildScatter() {
        Group g = new Group();
        g.setSize(400f, 400f);

        float cx = 200f, cy = 200f, radius = SIZE * 2f;

        ChainBox start = createStart(focusing.bullet.bulletEnum);
        start.setSize(SIZE, SIZE);
        start.setPosition(cx - SIZE / 2f, cy - SIZE / 2f);
        g.addActor(start);

        for (int i = 0; i < 8; i++) {
            float angle = i * 45f;
            ChainBox s = createChainBox(i);
            if (focusing.lower[i] != null) {
                s.setImage(focusing.lower[i].bullet.bulletEnum);
            }
            s.setSize(SIZE, SIZE);
            float x = cx + MathUtils.cosDeg(angle) * radius;
            float y = cy + MathUtils.sinDeg(angle) * radius;
            s.setPosition(x - SIZE / 2f, y - SIZE / 2f);
            g.addActor(s);
        }
        return g;
    }

    private Group buildHorizontalRow() {
        Table t = new Table();

        ChainBox start = createStart(focusing.bullet.bulletEnum);
        t.add(start).size(SIZE, SIZE).spaceRight(50f);

        for (int i = 0; i < 8; i++) {
            ChainBox s = createChainBox(i);
            if (focusing.lower[i] != null) {
                s.setImage(focusing.lower[i].bullet.bulletEnum);
            }
            t.add(s).size(SIZE, SIZE)
                .spaceRight(i < 7 ? 8f : 0f);
        }
        return t;
    }

    private ChainBox createStart(BulletEnum bullet) {
        ChainBox chainBox = new ChainBox(-1);
        chainBox.setImage(bullet);
        chainBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() > 1) {
                    changeFocus(focusing.upper);
//                    src = src.substring(0,src.length()-3);
                }
            }
        });
        return chainBox;
    }

    private ChainBox createChainBox(int index) {
        ChainBox chainBox = new ChainBox(index);
        dragAndDrop.addSource(new DragAndDrop.Source(chainBox) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                ChainContext chainContext;
                if (index == -1) {
                    chainContext = focusing;
                    main = null;
                    focusing = null;
                } else {
                    chainContext = focusing.lower[index];
                    focusing.setChain(null, index);
                }
                BulletBox pick = new BulletBox(chainContext.bullet.bulletEnum);
                payload.setDragActor(pick);
                payload.setObject(chainContext);

                chainBox.setImage(null);
                return payload;
            }
        });
        dragAndDrop.addTarget(new DragAndDrop.Target(chainBox) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                ChainContext chainContext = (ChainContext) payload.getObject();
                if (index == -1) {
                    main = chainContext;
                    focusing = chainContext;
                } else {
                    focusing.setChain(chainContext, index);
                }
                chainBox.setImage(chainContext.bullet.bulletEnum);
            }
        });
        chainBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() > 1) {
                    if (index == -1) {
                        changeFocus(focusing);
//                        if (focusing!=null && focusing.bullet.bulletEnum.chain){
//                            src = src+"/";
//                        }
                    } else {
                        if (focusing.lower[index] != null) {
                            changeFocus(focusing.lower[index]);
//                            src = src+(index+1)+"/";
                        }
                    }
                }
            }
        });
        return chainBox;
    }

    private void changeFocus(ChainContext chainContext) {
        focusing = chainContext;
        if (chainContext == null) {
            switchLayout(null);
            focusing = main;
        } else {
            switchLayout(chainContext.bullet.bulletEnum);
        }
    }

    private void buildInformationTable() {
        Table informationTable = new Table();
        Label playerCost = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        Label mainCost = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        Label focusCost = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        Table cost = new Table() {
            @Override
            public void act(float delta) {
                super.act(delta);
                playerCost.setText(resources.getString(TextRecord.Text_PlayerCost) + resources.getString(TextRecord.Text_Money) + playerData.cost);
                mainCost.setText(resources.getString(TextRecord.Text_MainCost) + resources.getString(TextRecord.Text_Money) + (main == null ? 0 : main.cost));
                focusCost.setText(resources.getString(TextRecord.Text_FocusCost) + resources.getString(TextRecord.Text_Money) + (main == null ? 0 : focusing.cost));
            }
        };
        cost.add(playerCost).fillX().expandX().row();
        cost.add(mainCost).fillX().expandX().row();
        cost.add(focusCost).fillX().expandX().row();
        informationTable.add(cost).width(SIZE * 2).left().padLeft(50f).padRight(50f);

        Label bulletsrc = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        Label bulletInformation = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        Label bulletCost = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        Table information = new Table() {
            @Override
            public void act(float delta) {
                bulletsrc.setText(src);
                bulletInformation.setText(resources.getString(select != null ? select.src : TextRecord.Text_Defalut));
                bulletCost.setText(select != null ? resources.getString(TextRecord.Text_Money) + select.cost : "");
            }
        };
        information.add(bulletsrc).fillX().fillY().expandX().row();
        information.add(bulletInformation).fillX().fillY().expandX().row();
        information.add(bulletCost).fillX().fillY().expandX().row();
        informationTable.add(information).fillX().fillY().expandX();

        SelectBox selectBox1 = new SelectBox(() -> target1 = true, RegionRecord.Gun1);
        SelectBox selectBox2 = new SelectBox(() -> target1 = false, RegionRecord.Gun2);
        Table select = new Table() {
            @Override
            public void act(float delta) {
                super.act(delta);
                selectBox1.setFocus(target1);
                selectBox2.setFocus(!target1);
            }
        };
        select.add(selectBox1).size(SIZE / 2).left();
        select.add(selectBox2).size(SIZE / 2).right().row();
        select.add(new UnderLineButton(resources, resources.getString(TextRecord.Text_Create), 0.15f, this::create)).colspan(2).center().row();
        select.add(new UnderLineButton(resources, resources.getString(TextRecord.Text_Save), 0.15f, this::saveWeapon)).colspan(2).center();
        informationTable.add(select).right().padRight(50f);
        add(informationTable).fillX().expandX().bottom().row();
    }

    private void saveWeapon() {
        int add = 0;
        if (target1) {
            if (playerData.weapon1 != null) add = playerData.weapon1.cost;
            playerData.weapon1 = main;
            playerData.bulletChain1 = null;
        } else {
            if (playerData.weapon2 != null) add = playerData.weapon2.cost;
            playerData.weapon2 = main;
            playerData.bulletChain2 = null;
        }
        signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Sub, resources.getString(target1 ? TextRecord.Text_SaveTitle1 : TextRecord.Text_SaveTitle2), 1f));
        signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Sub, "-" + resources.getString(TextRecord.Text_Money) + main.cost, 1f));
        playerData.cost -= main.cost;
        add /= 2;
        signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Sub, "+" + resources.getString(TextRecord.Text_Money) + add, 1f));
        playerData.cost += add;
        create();
        Save.save();
        signal.dispatch(new PlaySoundEvent(AudioRecord.PickUp, 0.5f, 0.5f, 0));
    }

    private void buildBulletTable() {
        bulletTable = new Table();
        rebuildBulletTable();
        add(bulletTable).bottom();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (bulletTable != null) rebuildBulletTable();
    }

    private void rebuildBulletTable() {
        bulletTable.clear();
        buildBullet(BulletEnum.Normal);
        buildBullet(BulletEnum.Splitter);
        buildBullet(BulletEnum.Bouncy);
        if (playerData.stage1) {
            buildBullet(BulletEnum.Bomb);
            buildBullet(BulletEnum.Dropper);
            buildBullet(BulletEnum.Emitter);
        }
        if (playerData.stage2) {
            buildBullet(BulletEnum.Ballistic);
            buildBullet(BulletEnum.Fixed);
        }
    }

    private void buildBullet(BulletEnum bulletEnum) {
        BulletBox bulletBox = new BulletBox(bulletEnum);
        bulletTable.add(bulletBox).size(SIZE);
        dragAndDrop.addSource(new DragAndDrop.Source(bulletBox) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (bulletBox.baned) {
                    return null;
                }
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                BulletBox pick = new BulletBox(bulletEnum);
                payload.setDragActor(pick);
                payload.setObject(pullBulletChain(bulletEnum));
                return payload;
            }
        });
        bulletBox.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
                select = bulletEnum;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, @Null Actor toActor) {
                select = null;
            }
        });
    }

    private ChainContext pullBulletChain(BulletEnum bulletEnum) {
        try {
            return new ChainContext(focusing, bulletEnum.bulletContext.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void create() {
        main = null;
        focusing = null;
//        src = "";
        switchLayout(null);
        signal.dispatch(new ShowTitleEvent(ToastOverlay.Type.Sub, resources.getString(TextRecord.Text_CreateTitle), 1f));
    }

    private class ChainBox extends Stack {
        final int index;
        Image image;
        Image chain;

        public ChainBox(int index) {
            this.index = index;
            Image backgroud = new Image(resources.getSkin().getDrawable(RegionRecord.BulletBackground));
            add(backgroud);
            image = new Image();
            add(image);
            chain = new Image(resources.getAtlas().findRegion(RegionRecord.Chain));
            add(chain);
            chain.setVisible(false);
        }

        public void setImage(BulletEnum bulletEnum) {
            if (bulletEnum == null) {
                image.setDrawable(null);
                chain.setVisible(false);
            } else {
                image.setDrawable(new TextureRegionDrawable(resources.getAtlas().findRegion(bulletEnum.name())));
                if (bulletEnum.chain) chain.setVisible(true);
            }
        }
    }

    private class SelectBox extends Stack {
        Image select;

        public SelectBox(Runnable runnable, String src) {
            select = new Image(resources.getSkin().getDrawable(RegionRecord.WeaponBackground));
            add(select);
            Image image = new Image(resources.getAtlas().findRegion(src));
            add(image);
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    runnable.run();
                }
            });
        }

        public void setFocus(boolean focus) {
            select.setVisible(focus);
        }
    }

    private class BulletBox extends Stack {
        boolean baned = false;
        Image ban;
        BulletEnum bulletEnum;

        public BulletBox(BulletEnum bulletEnum) {
            this.bulletEnum = bulletEnum;
            Image backgroud = new Image(resources.getSkin().getDrawable(RegionRecord.BulletBackground));
            add(backgroud);
            TextureRegion region = resources.getAtlas().findRegion(bulletEnum.name());
            Image image = new Image(region);
            add(image);

            if (bulletEnum.chain) {
                Image chain = new Image(resources.getAtlas().findRegion(RegionRecord.Chain));
                add(chain);
            }

            ban = new Image(resources.getAtlas().findRegion(RegionRecord.Ban));
            ban.setVisible(false);
            add(ban);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            boolean baned;
            if (main != null) {
                baned = playerData.cost - main.cost < bulletEnum.cost;
            } else {
                baned = playerData.cost < bulletEnum.cost;
            }
            setBaned(baned);
        }

        public void setBaned(boolean baned) {
            this.baned = baned;
            ban.setVisible(baned);
        }
    }
}
