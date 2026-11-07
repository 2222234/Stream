package com.mobius.stream.Game.Dialogue;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class DialogueDatabase {
    public Array<DialogueData> dialogues = new Array<>();

    private ObjectMap<String, DialogueData> byId;

    public DialogueData get(String id) {
        if (byId == null) {
            byId = new ObjectMap<>();
            for (DialogueData d : dialogues) {
                if (d.id != null && !d.id.isEmpty()) {
                    byId.put(d.id, d);
                }
            }
        }
        return byId.get(id);
    }
}
