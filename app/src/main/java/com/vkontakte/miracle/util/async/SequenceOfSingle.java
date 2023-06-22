package com.vkontakte.miracle.util.async;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SequenceOfSingle {

    private final ArrayList<Single<Object>> singles = new ArrayList<>();
    private final ArrayList<Consumer<Object>> successConsumers = new ArrayList<>();
    private final ArrayList<Consumer<Throwable>> throwableConsumers = new ArrayList<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final PublishSubject<Single<Object>> iterableSubject = PublishSubject.create();
    private final Disposable iterableDisposable = iterableSubject.subscribe(single -> {
        Consumer<Object> successConsumer = successConsumers.get(0);
        Consumer<Throwable> errorConsumer = throwableConsumers.get(0);
        Disposable singleDisposable = single
                .subscribe(successObject -> {
                    successConsumer.accept(successObject);
                    nextSingle();
                }, throwable -> {
                    errorConsumer.accept(throwable);
                    nextSingle();
                });
        compositeDisposable.add(singleDisposable);
    }, throwable -> {
        nextSingle();
    });

    {
        compositeDisposable.add(iterableDisposable);
    }

    private void nextSingle(){
        successConsumers.remove(0);
        throwableConsumers.remove(0);
        singles.remove(0);
        if(!singles.isEmpty()) {
            iterableSubject.onNext(singles.get(0));
        }
    }

    public void dispose(){
        compositeDisposable.dispose();
    }

    public void clear(){
        successConsumers.clear();
        throwableConsumers.clear();
        singles.clear();
        compositeDisposable.clear();
    }

    public void add(@NonNull Single<Object> single,
                    @NonNull Consumer<Object> onSuccess,
                    @NonNull Consumer<Throwable> onError){
        successConsumers.add(onSuccess);
        throwableConsumers.add(onError);
        singles.add(single);
        if(singles.size()==1){
            iterableSubject.onNext(single);
        }
    }

    public void addToTop(@NonNull Single<Object> single,
                    @NonNull Consumer<Object> onSuccess,
                    @NonNull Consumer<Throwable> onError){
        if(singles.isEmpty()){
            add(single,onSuccess,onError);
        } else {
            successConsumers.add(1,onSuccess);
            throwableConsumers.add(1,onError);
            singles.add(1,single);
        }
    }

    public void addUnder(@NonNull Single<Object> above,
                         @NonNull Single<Object> single,
                         @NonNull Consumer<Object> onSuccess,
                         @NonNull Consumer<Throwable> onError){
        if(singles.isEmpty()){
            add(single,onSuccess,onError);
        } else {
            int position = singles.indexOf(above);
            if(position<0){
                successConsumers.add(onSuccess);
                throwableConsumers.add(onError);
                singles.add(single);
            } else {
                successConsumers.add(position+1,onSuccess);
                throwableConsumers.add(position+1,onError);
                singles.add(position+1,single);
            }
        }
    }

}
