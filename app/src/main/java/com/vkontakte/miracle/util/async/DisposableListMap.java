package com.vkontakte.miracle.util.async;

import androidx.collection.ArrayMap;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class DisposableListMap <K> extends ArrayMap<K, List<Disposable>> {

    @Override
    public void clear() {
        dispose();
        super.clear();
    }

    public void dispose(){
        for (Entry<K,List<Disposable>> entry:entrySet()) {
            for (Disposable disposable:entry.getValue()) {
                if(disposable!=null){
                    disposable.dispose();
                }
            }
        }
    }
}