package com.bignerdranch.android.tples122

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val observable =  Observable.fromCallable { longAction("a") }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                Integer.parseInt(it)
            }
            .onErrorResumeNext {
                Log.d("D/RX_TAG","[${Thread.currentThread().name}] Ой, ошибка! Начинай обратный отсчет")
                Observable.fromArray(13,12,11,10,9,8,7,6,5,4,3,2,1,0)
            }


        val disposable: Disposable = observable.subscribe(
                {Log.d("D/RX_TAG","[${Thread.currentThread().name}] next value = $it")},
                {Log.d("D/RX_TAG", "[${Thread.currentThread().name}] Ой, ошибка! Начинай обратный отсчет")},
                {Log.d("D/RX_TAG","[${Thread.currentThread().name}] БУМ! Закончили")}
            )

        Thread {
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d("D/RX_TAG", "[${Thread.currentThread().name}] Потока больше нет - отписались")
                disposable.dispose()
            }, 1000)
        }.start()

    }

    private fun longAction(text: String): String {

        val observable: Observable<Int> = Observable.fromArray(0,1,2,3,4,5,6,7,8,9,10,11,12,13)
            .subscribeOn(Schedulers.computation())

        val observerOne: Observer<Int> = object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("D/RX_TAG","[${Thread.currentThread().name}] Подписались")
            }

            override fun onNext(t: Int) {
                Log.d("D/RX_TAG","[${Thread.currentThread().name}] next value = $t")
            }

            override fun onError(e: Throwable) {
                Log.d("D/RX_TAG","[${Thread.currentThread().name}] Ой, ошибка! Начинай обратный отсчет")
            }

            override fun onComplete() {}
        }

        observable.subscribe(observerOne)
        return text
    }
}