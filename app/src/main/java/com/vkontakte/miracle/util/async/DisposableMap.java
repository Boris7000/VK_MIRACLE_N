package com.vkontakte.miracle.util.async;

import androidx.collection.ArrayMap;

import io.reactivex.rxjava3.disposables.Disposable;

public class DisposableMap<K> extends ArrayMap<K, Disposable> {

    @Override
    public void clear() {
        dispose();
        super.clear();
    }

    public void dispose(){
        for (Entry<K,Disposable> entry:entrySet()) {
            Disposable disposable = entry.getValue();
            if(disposable!=null){
                disposable.dispose();
            }
        }
    }
}
