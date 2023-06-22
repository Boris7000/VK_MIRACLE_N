package com.vkontakte.miracle.model.messages.fields;

import android.util.ArrayMap;

import com.miracle.engine.recyclerview.TypedData;
import com.vkontakte.miracle.util.constants.TypedDataConstants;

import java.util.Map;
import java.util.Objects;

public class ChatActions implements TypedData {

    public enum Type{
        TYPING_MESSAGE,
        RECORDING_VOICE_MESSAGE,
        MIXED,
        EMPTY
    }

    private Type averageType = Type.EMPTY;
    private final Map<String,Type> actions = new ArrayMap<>();

    public boolean addMemberIds(String[] newMemberIds, Type type){
        boolean hasUpdates = false;
        for (String memberId:newMemberIds) {
            if(actions.put(memberId,type)!=type){
                hasUpdates = true;
            }
        }
        if(hasUpdates){
            calculateAverageType();
        }
        return hasUpdates;
    }

    public boolean removeMemberId(String removingMemberId, Type type){
        boolean hasUpdates = false;
        if(type==Type.MIXED){
            if(actions.remove(removingMemberId)!=null){
                hasUpdates = true;
            }
        } else if(actions.remove(removingMemberId, type)){
            hasUpdates = true;
        }
        if(hasUpdates){
            calculateAverageType();
        }
        return hasUpdates;
    }
    public boolean removeMemberIds(String[] removingMemberIds, Type type){
        boolean hasUpdates = false;
        for (String memberId:removingMemberIds) {
            if(type==Type.MIXED){
                if(actions.remove(memberId)!=null){
                    hasUpdates = true;
                }
            } else if(actions.remove(memberId, type)){
                hasUpdates = true;
            }
        }
        if(hasUpdates){
            calculateAverageType();
        }
        return hasUpdates;
    }

    private void calculateAverageType(){
        if(actions.isEmpty()){
            averageType = Type.EMPTY;
        } else {
            Type type = null;
            for (Map.Entry<String,Type> entry:actions.entrySet()) {
                if(type==null){
                    type = entry.getValue();
                } else {
                    if(type!=entry.getValue()){
                        type = Type.MIXED;
                        break;
                    }
                }
            }
            averageType = type;
        }
    }

    public Type getAverageType() {
        return averageType;
    }

    public Map<String, Type> getActions() {
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatActions that = (ChatActions) o;
        return averageType == that.averageType && actions.equals(that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(averageType, actions);
    }

    @Override
    public int getDataType() {
        switch (averageType){
            case TYPING_MESSAGE:{
                return TypedDataConstants.TYPE_CHAT_ACTION_TYPING_MESSAGE;
            }
            case RECORDING_VOICE_MESSAGE:{
                return TypedDataConstants.TYPE_CHAT_ACTION_RECORDING_VOICE_MESSAGE;
            }
            case MIXED:{
                return TypedDataConstants.TYPE_CHAT_ACTION_MIXED;
            }
        }
        return TypedDataConstants.TYPE_PLACEHOLDER;
    }
}
