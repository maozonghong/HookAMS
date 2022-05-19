package com.example.rxjava;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.reactivestreams.Subscriber;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maozonghong
 * <p>
 * on 4/25/21
 **/
public class DoSubscribeActivity extends AppCompatActivity {

    public static final String TAG="DoSubscribeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file=new File("/mnt/sdcard/test/test.txt");
        Log.e(TAG,file.getParent());


        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onComplete();
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.e(TAG,"00doOnSubscribe在线程" + Thread.currentThread().getName() + "中");
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(@NonNull Integer integer) throws Exception {
                        Log.e(TAG,"map1在线程" + Thread.currentThread().getName() + "中");
                        return integer + "";
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {

                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.e(TAG,"11doOnSubscribe在线程" + Thread.currentThread().getName() + "中");
                    }
                })

                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String s) throws Exception {
                        Log.e(TAG,"map2在线程" + Thread.currentThread().getName() + "中");
                        return s + "1";
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {

                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Log.e(TAG,"22doOnSubscribe在线程" + Thread.currentThread().getName() + "中");
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG,"onNext在线程" + Thread.currentThread().getName() + "中");
                    }
                });
    }
}
