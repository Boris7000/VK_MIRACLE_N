package com.vkontakte.miracle.model.general;

import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

public class Error implements TypedData {

    private final String errorMessage;

    public Error(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public int getDataType() {
        return TypedDataConstants.TYPE_ERROR;
    }

}
