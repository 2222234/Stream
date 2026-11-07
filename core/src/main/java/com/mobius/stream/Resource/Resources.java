package com.mobius.stream.Resource;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.mobius.stream.Game.Animate.AnimationCache;
import com.mobius.stream.Game.Dialogue.DialogueData;
import com.mobius.stream.Game.Dialogue.DialogueDatabase;
import com.mobius.stream.Game.Dialogue.DialogueLine;
import com.mobius.stream.Game.Dialogue.DialogueLoader;

import java.util.Locale;

import static com.mobius.stream.Resource.ResourcesRecord.*;

public class Resources implements Disposable {
    private final AssetManager assets;
    private final StringBuilder chars = new StringBuilder();
    AnimationCache animationCache;
    private LoadStage stage = LoadStage.Basic;
    private boolean loaded = false;
    private Skin skin;

    public Resources() {
        assets = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.setLoader(BitmapFont.class, new FreetypeFontLoader(resolver));
        assets.setLoader(DialogueDatabase.class, new DialogueLoader(assets.getFileHandleResolver()));
        assets.setLoader(TiledMap.class, new AtlasTmxMapLoader(new InternalFileHandleResolver()));
    }

    public void start() {
        assets.load(I18n, I18NBundle.class
            , new I18NBundleLoader.I18NBundleParameter(Locale.CHINA, "UTF-8"));

        assets.load(Texture, TextureAtlas.class);

        assets.load(Map_Home, TiledMap.class);
        assets.load(Map_Station, TiledMap.class);
        assets.load(Map_Mall, TiledMap.class);
        assets.load(Map_School, TiledMap.class);
        assets.load(Map_Demo, TiledMap.class);

        assets.load(Dialogue, DialogueDatabase.class);

        for (AudioRecord audioRecord : AudioRecord.values()) {
            if (audioRecord.kind == AudioRecord.Kind.MUSIC) {
                assets.load(audioRecord.path, Music.class);
            }
            if (audioRecord.kind == AudioRecord.Kind.SOUND) {
                assets.load(audioRecord.path, Sound.class);
            }
        }
    }

    public boolean update() {
        if (loaded) return true;

        boolean finished = assets.update();
        if (!finished) return false;

        switch (stage) {
            case Basic:
                stage = LoadStage.Font;
                loadFont();
                return false;
            case Font:
                stage = LoadStage.Skin;
                buildSkin();
                return false;
            case Skin:
                stage = LoadStage.Done;
                loaded = true;
                return true;
            default:
                return true;
        }
    }

    private void loadFont() {
        I18NBundle i18n = assets.get(I18n, I18NBundle.class);
        chars.append(FreeTypeFontGenerator.DEFAULT_CHARS);
        for (String key : i18n.keys()) {
            chars.append(i18n.get(key));
        }
        DialogueDatabase dialogueDatabase = assets.get(Dialogue, DialogueDatabase.class);
        for (DialogueData dialogueData : dialogueDatabase.dialogues) {
            for (DialogueLine dialogueLine : dialogueData.lines) {
                chars.append(dialogueLine.name);
                chars.append(dialogueLine.text);
            }
        }

        for (MapRecord.Map map : MapRecord.Map.values()) {
            TiledMap tiledMap = assets.get(map.name, TiledMap.class);
            chars.append(tiledMap.getProperties().get(MapRecord.MapProperties.Name.name(), String.class));
            MapLayer roadSign = tiledMap.getLayers().get(MapRecord.Layer.RoadSign.name());
            if (roadSign != null) {
                for (MapObject object : roadSign.getObjects()) {
                    chars.append(object.getName());
                }
            }
        }

        font(Min_Font, 16);
        font(Mid_Font, 32);
        font(Max_Font, 64);
        font(Large_Font, 128);
    }

    private void font(String fileName, int size) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = Font;
        params.fontParameters.size = size;
        params.fontParameters.characters = chars.toString();
        params.fontParameters.borderWidth = 1f;
        params.fontParameters.borderColor = Color.BLACK;
        params.fontParameters.flip = false;
        assets.load(fileName, BitmapFont.class, params);
    }

    private void buildSkin() {
        skin = new Skin();

        TextureAtlas uiAtlas = assets.get(Texture);

        skin.add(RegionRecord.UnderLine, new NinePatch(uiAtlas.findRegion(RegionRecord.UnderLine), 1, 1, 1, 1), NinePatch.class);
        skin.add(RegionRecord.WeaponBackground, new NinePatch(uiAtlas.findRegion(RegionRecord.WeaponBackground), 1, 1, 1, 1), NinePatch.class);
        skin.add(RegionRecord.BulletBackground, new NinePatch(uiAtlas.findRegion(RegionRecord.BulletBackground), 2, 2, 2, 2), NinePatch.class);

        lable(Min_Font);
        lable(Mid_Font);
        lable(Max_Font);
        lable(Large_Font);
    }

    private void lable(String fontName) {
        Label.LabelStyle smallLabelStyle = new Label.LabelStyle();
        smallLabelStyle.font = assets.get(fontName);
        smallLabelStyle.fontColor = Color.WHITE;
        skin.add(fontName, smallLabelStyle);
    }

    public Skin getSkin() {
        if (!loaded) throw new IllegalStateException("Resources not loaded yet");
        return skin;
    }

    public TextureAtlas getAtlas() {
        if (!loaded) throw new IllegalStateException("Resources not loaded yet");
        return assets.get(Texture);
    }

    public DialogueDatabase getDatabase() {
        if (!loaded) throw new IllegalStateException("Resources not loaded yet");
        return assets.get(Dialogue);
    }

    public TiledMap getMap(MapRecord.Map map) {
        if (!loaded) throw new IllegalStateException("Resources not loaded yet");
        return assets.get(map.name);
    }

    public AnimationCache getAnimationCache() {
        if (animationCache == null) {
            animationCache = new AnimationCache(getAtlas());
        }
        return animationCache;
    }

    public Music music(AudioRecord audioRecord) {
        return this.assets.get(audioRecord.path, Music.class);
    }

    public Sound sound(AudioRecord audioRecord) {
        return this.assets.get(audioRecord.path, Sound.class);
    }

    public String getString(String key) {
        return assets.get(I18n, I18NBundle.class).get(key);
    }

    public <T> T get(String fileName, Class<T> type) {
        return assets.get(fileName, type, true);
    }

    @Override
    public void dispose() {
        if (skin != null) skin.dispose();
        assets.dispose();
    }

    private enum LoadStage {
        Basic, Font, Skin, Done
    }
}
