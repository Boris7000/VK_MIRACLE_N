package com.vkontakte.miracle.model.users.fileds;

import com.miracle.engine.annotation.MayBeEmpty;

import org.json.JSONObject;

public class Acc {

    private final String firstNameAcc;
    private final String lastNameAcc;
    private final String fullNameAcc;

    public Acc(JSONObject jsonObject) {
        firstNameAcc = jsonObject.optString("first_name_acc");
        lastNameAcc = jsonObject.optString("last_name_acc");

        if(!lastNameAcc.isEmpty()){
            fullNameAcc = String.format("%s %s", firstNameAcc, lastNameAcc);
        } else {
            fullNameAcc = firstNameAcc;
        }
    }

    @MayBeEmpty
    public String getFirstName() {
        return firstNameAcc;
    }

    @MayBeEmpty
    public String getLastName() {
        return lastNameAcc;
    }

    @MayBeEmpty
    public String getFullName() {
        return fullNameAcc;
    }
}
