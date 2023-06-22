package com.miracle.engine.storage;

import android.util.ArrayMap;

import java.util.concurrent.ThreadLocalRandom;

public class LargeDataStorage {

    private final ArrayMap<String,Object> sparseArray = new ArrayMap<>();
    private static LargeDataStorage instance;

    public static LargeDataStorage get(){
        if (null == instance){
            instance = new LargeDataStorage();
        }
        return instance;
    }

    public String storeLargeData(Object data, String key){
        sparseArray.put(key,data);
        return key;
    }

    public String storeLargeData(Object data){
        String key = createUniqueKey();
        sparseArray.put(key,data);
        return key;
    }

    public Object extractLargeData(String key){
        Object object = sparseArray.get(key);
        removeLargeData(key);
        return object;
    }

    public Object getLargeData(String key){
        return sparseArray.get(key);
    }

    public void removeLargeData(String key){
        sparseArray.remove(key);
    }

    public String createUniqueKey(){

        StringBuilder key = new StringBuilder();

        for (int i=0; i<6; i++) key.append(ThreadLocalRandom.current().nextInt(0, 9 + 1));

        String key1 = key.toString();

        if(sparseArray.get(key1)==null) return key1;
        else return createUniqueKey();
    }

}
