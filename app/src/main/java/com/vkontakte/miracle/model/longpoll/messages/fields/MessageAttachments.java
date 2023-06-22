package com.vkontakte.miracle.model.longpoll.messages.fields;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageAttachments {

    private final String[][] attachments;

    public MessageAttachments(JSONObject jsonObject) throws JSONException {
        attachments = new String[jsonObject.length()/2][2];

        if(jsonObject.has("attach1")){
            attachments[0] = new String[]{jsonObject.getString("attach1_type"),
                    jsonObject.getString("attach1_type")};
        } else return;
        if(jsonObject.has("attach2")){
            attachments[1] = new String[]{jsonObject.getString("attach2_type"),
                    jsonObject.getString("attach2_type")};
        } else return;
        if(jsonObject.has("attach3")){
            attachments[2] = new String[]{jsonObject.getString("attach3_type"),
                    jsonObject.getString("attach3_type")};
        } else return;
        if(jsonObject.has("attach4")){
            attachments[3] = new String[]{jsonObject.getString("attach4_type"),
                    jsonObject.getString("attach4_type")};
        } else return;
        if(jsonObject.has("attach5")){
            attachments[4] = new String[]{jsonObject.getString("attach5_type"),
                    jsonObject.getString("attach5_type")};
        } else return;
        if(jsonObject.has("attach6")){
            attachments[5] = new String[]{jsonObject.getString("attach6_type"),
                    jsonObject.getString("attach6_type")};
        } else return;
        if(jsonObject.has("attach7")){
            attachments[6] = new String[]{jsonObject.getString("attach7_type"),
                    jsonObject.getString("attach7_type")};
        } else return;
        if(jsonObject.has("attach8")){
            attachments[7] = new String[]{jsonObject.getString("attach8_type"),
                    jsonObject.getString("attach8_type")};
        } else return;
        if(jsonObject.has("attach9")){
            attachments[8] = new String[]{jsonObject.getString("attach9_type"),
                    jsonObject.getString("attach9_type")};
        } else return;
        if(jsonObject.has("attach10")){
            attachments[9] = new String[]{jsonObject.getString("attach10_type"),
                    jsonObject.getString("attach10_type")};
        }
    }

    public String[][] getAttachments() {
        return attachments;
    }
}
