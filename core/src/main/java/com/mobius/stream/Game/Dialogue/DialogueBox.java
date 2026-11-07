package com.mobius.stream.Game.Dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.mobius.stream.Resource.DialoguesRecord;
import com.mobius.stream.Resource.Resources;
import com.mobius.stream.Resource.ResourcesRecord;

public class DialogueBox extends Table {
    private static final String TAG = "DialogueBox";
    private static final float DEFAULT_CHAR_SPEED = 0.01f;
    private static final float AVATAR_SIZE = 200f;
    private static final float EDGE_PADDING = 40f;
    private static final float GAP = 100f;
    private static final float TEXT_WIDTH_RATIO = 0.60f;
    private final TextureAtlas atlas;
    private final DialogueDatabase database;
    private final Image avatarImage;
    private final Label nameLabel;
    private final Label textLabel;
    private Array<DialogueLine> lines;
    private int lineIndex;
    private int visibleChars;
    private float charTimer;
    private float charSpeed = DEFAULT_CHAR_SPEED;
    private String fullText = "";
    private boolean typing;
    private boolean running;
    private String currentId;
    private OnDialogueFinished finishedCallback;
    private boolean visible = false;

    public DialogueBox(Resources resources) {
        atlas = resources.getAtlas();
        database = resources.getDatabase();

        avatarImage = new Image();
        avatarImage.setScaling(Scaling.fit);

        nameLabel = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        nameLabel.setAlignment(Align.center);

        textLabel = new Label("", resources.getSkin(), ResourcesRecord.Mid_Font);
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.left);

        Table leftCol = new Table();
        leftCol.setTouchable(Touchable.disabled);
        leftCol.add(avatarImage).size(AVATAR_SIZE, AVATAR_SIZE);
        leftCol.row();
        leftCol.add(nameLabel).width(AVATAR_SIZE).padTop(8f);

        setFillParent(true);
        setTouchable(Touchable.disabled);
        bottom().left().pad(EDGE_PADDING);

        add(leftCol).left().bottom();
        add(textLabel)
            .width(Value.percentWidth(TEXT_WIDTH_RATIO, this))
            .height(AVATAR_SIZE)
            .padLeft(GAP)
            .left();

        setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    advance();
                    return true;
                }
                return false;
            }
        });

        setVisible(false);
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isRunning() {
        return running;
    }

    public void start(DialoguesRecord dialoguesRecord, OnDialogueFinished callback) {
        if (running) {
            Gdx.app.error(TAG, "already running");
            return;
        }
        DialogueData data = database == null ? null : database.get(dialoguesRecord.id);
        if (data == null || data.lines == null || data.lines.size == 0) {
            Gdx.app.error(TAG, "unknown or empty dialogue id: " + dialoguesRecord.id);
            return;
        }
        this.currentId = dialoguesRecord.id;
        this.lines = data.lines;
        this.finishedCallback = callback;
        this.running = true;
        this.lineIndex = 0;

        showLine(0);
        this.visible = true;
        setVisible(true);
    }

    public void advance() {
        if (!running) return;

        if (typing) {
            visibleChars = fullText.length();
            textLabel.setText(fullText);
            typing = false;
            return;
        }
        if (lineIndex + 1 < lines.size) {
            showLine(lineIndex + 1);
        } else {
            finish();
        }
    }

    public void skipAll() {
        if (!running) return;
        typing = false;
        visibleChars = fullText.length();
        textLabel.setText(fullText);
        finish();
    }

    private void showLine(int index) {
        if (lines == null || index >= lines.size) {
            finish();
            return;
        }
        lineIndex = index;
        DialogueLine line = lines.get(index);

        if (line.avatar != null) {
            if (line.avatar.isEmpty()) {
                avatarImage.setVisible(false);
            } else {
                TextureRegion region = atlas.findRegion(line.avatar);
                if (region == null) {
                    Gdx.app.error(TAG, "atlas region not found: " + line.avatar);
                    avatarImage.setVisible(false);
                } else {
                    avatarImage.setDrawable(new TextureRegionDrawable(region));
                    avatarImage.setVisible(true);
                }
            }
        }

        if (line.name != null) {
            nameLabel.setText(line.name);
        }

        fullText = line.text == null ? "" : line.text;
        charSpeed = line.speed != null ? line.speed : DEFAULT_CHAR_SPEED;
        visibleChars = 0;
        charTimer = 0f;
        typing = !fullText.isEmpty();
        textLabel.setText("");
    }

    private void finish() {
        if (!running) return;
        running = false;
        visible = false;
        setVisible(false);

        if (finishedCallback != null) {
            finishedCallback.onFinished(this, currentId);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!running || !typing) return;

        charTimer += delta;
        if (charSpeed <= 0f) {
            visibleChars = fullText.length();
        } else {
            while (charTimer >= charSpeed && visibleChars < fullText.length()) {
                charTimer -= charSpeed;
                visibleChars++;
            }
        }
        textLabel.setText(fullText.substring(0, visibleChars));

        if (visibleChars >= fullText.length()) {
            typing = false;
        }
    }

    public interface OnDialogueFinished {
        void onFinished(DialogueBox box, String dialogueId);
    }
}
