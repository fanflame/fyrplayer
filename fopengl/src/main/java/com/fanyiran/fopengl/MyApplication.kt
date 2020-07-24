package com.fanyiran.fopengl

import android.app.Application

class MyApplication : Application() {
    companion object {
        public lateinit var myApplication: MyApplication;
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
    }
}