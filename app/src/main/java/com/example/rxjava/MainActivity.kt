package com.example.rxjava

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    val TAG="MainActivity"

    var liveData= MutableLiveData<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        /***Observable.create(object:ObservableOnSubscribe<String>{
            override fun subscribe(emitter: ObservableEmitter<String>) {
                Log.e(TAG,"subscribe:${Thread.currentThread().name}")
                emitter.onNext("1")
                emitter.onNext("2")
            }

        }).doOnSubscribe {
            Log.e(TAG,"doOnSubscribe:${Thread.currentThread().name}")
        }.subscribeOn(Schedulers.single()).map { t ->
            Log.e(TAG, "map:${Thread.currentThread().name}")
            t + "rx"
        }.observeOn(AndroidSchedulers.mainThread())

            .subscribe(object : Consumer<String> {
                override fun accept(t: String?) {
                    Log.e(TAG, "onNext:+${t}：${Thread.currentThread().name}")
                }

            }, { t ->Log.e(TAG,"onError") }, {Log.e(TAG,"onComplete") }, { d -> Log.e(TAG, "onSubscribe:${Thread.currentThread().name}") })**/


//      var flowable=  Observable.just(3.toLong()).delay(2, TimeUnit.SECONDS)
//
//        Observable.intervalRange(4, 6, 1, 1, TimeUnit.SECONDS).mergeWith(flowable).subscribe{
//            Log.e(TAG, "merge:"+it.toString())
//        }
//
//
//        Flowable.just(3, 2, 4).mergeWith(Flowable.just(100, 200))
//                .subscribe { integer -> println("concatWith, value：$integer") }

//        Observable.just(1,2,3,4).debounce(1,TimeUnit.MILLISECONDS).
//                subscribe{
//                    Log.e(TAG,it.toString())
//                }


//        var i=0
//        Observable.interval(0,1,TimeUnit.SECONDS)
//                .take(60)
//                .subscribe(object : Observer<Long> {
//                    override fun onNext(integer: Long) {
//                        i +=
//                        Log.e(TAG, "==================onNext $integer")
//                    }
//
//
//                    override fun onComplete() {
//                        Log.e(TAG, "==================onComplete ")
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        Log.e(TAG, "==================onSubscribe ")
//
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Log.e(TAG, "==================onSubscribe ")
//
//                    }
//                })


//        Observable.just(1,2,3,4,5,6).takeUntil {
//            it<1
//        }.subscribe { Log.e(TAG,"takeUntil:${it}") }



        liveData.observe(this){
            Log.e(TAG,it)
        }
        txt_livedata.setOnClickListener{
            liveData.value="6"
//            liveData.postValue("2")
//            liveData.postValue("4")
//            liveData.postValue("7")
            it.translationY=80f
            it.isSelected=true

            Toast.makeText(this,"toast",Toast.LENGTH_LONG).show()
        }


//        Observable.concat(Observable.just(1).filter { false },Observable.just(2,3,4))
//                .subscribe{
//                    Log.e(TAG,"concat:${it}")
//                }
//
//            Log.e(TAG,getExternalFilesDir(null)?.absolutePath?:"")
//            Log.e(TAG,getExternalFilesDir("download")?.absolutePath?:"")
//            Log.e(TAG,externalCacheDir?.absolutePath?:"")


       /* Observable.create<String> {
            it.onNext("1")
            it.onNext("2")
            it.onNext("3")
            it.onError(Exception("404"))
        }.retry(2).subscribe(object:Observer<String>{
            override fun onSubscribe(d: Disposable) {
                Log.e(TAG,"onSubscribe")
            }

            override fun onNext(t: String) {
                Log.e(TAG,"onNext:${t}")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG,"onError:${e.message}")
            }

            override fun onComplete() {
                Log.e(TAG,"onComplete")
            }

        })*/

        Observable.create<String> {
            it.onNext("5")
            it.onNext("6")
            it.onError(Exception("404"))
        }.retryWhen{
           return@retryWhen it.flatMap { it ->
               if(it.message.equals("404")) Observable.error(Throwable("终止了"))
               else Observable.just("可以忽略的异常")
            }

        }.subscribe(object:Observer<String>{
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                Log.e(TAG,"onNext....${t}")
            }

            override fun onError(e: Throwable) {
                Log.e(TAG,"onError:${e.message}")
            }

            override fun onComplete() {
                Log.e(TAG,"onComplete")

            }

        })

    }


    override fun onStop() {
        super.onStop()
        liveData.value="onStop7"
        liveData.value="onStop10"

    }

    override fun onResume() {
        super.onResume()

        liveData.value="onResume9"

    }


    override fun onPause() {
        super.onPause()
        liveData.value="onPause8"

    }
}