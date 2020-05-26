package com.royser.stetho_demo

import android.app.Application
import com.facebook.stetho.Stetho
import timber.log.Timber

/**
 * Created by Royser on 20/5/2020 AD.
 */
//TODO #1.2 : Create file Application
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //TODO #4.2 : implement DebugTree
        Timber.plant(DebugTree())

        //TODO #1.3 : Initial Stetho
        Stetho.initializeWithDefaults(this)
    }

}