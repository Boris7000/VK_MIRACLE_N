package com.vkontakte.miracle.model.messages.fields;

import com.vkontakte.miracle.model.general.JSONable;

import org.json.JSONException;
import org.json.JSONObject;

public class PushSettings implements JSONable {

    private final boolean disabledForever;
    private final boolean noSound;
    private final boolean disabledMentions;
    private final boolean disabledMassMentions;

    public PushSettings(JSONObject jsonObject) throws JSONException {
        disabledForever = jsonObject.getBoolean("disabled_forever");
        noSound = jsonObject.getBoolean("no_sound");
        disabledMentions = jsonObject.getBoolean("disabled_mentions");
        disabledMassMentions = jsonObject.getBoolean("disabled_mass_mentions");
    }

    public boolean isDisabledForever() {
        return disabledForever;
    }

    public boolean isNoSound() {
        return noSound;
    }

    public boolean isDisabledMentions() {
        return disabledMentions;
    }

    public boolean isDisabledMassMentions() {
        return disabledMassMentions;
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("disabled_forever", disabledForever);
        jsonObject.put("no_sound", noSound);
        jsonObject.put("disabled_mentions", disabledMentions);
        jsonObject.put("disabled_mass_mentions", disabledMassMentions);
        return jsonObject;
    }
}
