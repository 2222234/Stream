package com.mobius.stream.Game.Event.Ui;

import com.mobius.stream.Game.Dialogue.DialogueBox;
import com.mobius.stream.Game.Event.GameEvent;
import com.mobius.stream.Resource.DialoguesRecord;

public class StartDiologuesEvent extends GameEvent {
    public final DialoguesRecord dialoguesRecord;
    public final DialogueBox.OnDialogueFinished callback;

    public StartDiologuesEvent(DialoguesRecord dialoguesRecord, DialogueBox.OnDialogueFinished callback) {
        this.dialoguesRecord = dialoguesRecord;
        this.callback = callback;
    }
}
