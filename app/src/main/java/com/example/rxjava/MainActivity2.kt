package com.example.rxjava

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
Created by maozonghong

on 5/18/22
 **/
class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        HookHelper.hookAMSAidl()
        HookHelper.hookInstrumentation()
        HookHelper.hookHandler()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stub)

    }

    fun onClick(view: View) {
        startActivity(Intent().apply {
            setClass(view.context, TargetActivity::class.java)
        })
    }
}