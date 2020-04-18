package com.clawsmark.logtracker

import android.app.Application
import android.util.Log
import com.clawsmark.logtracker.di.appModule
import com.clawsmark.logtracker.loggy.Loggy
import com.clawsmark.logtracker.utils.trace
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    val tag = "App"
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule))
        }
        trace(tag, "onCreate")

        Loggy.context.logLevel = 3
        Loggy.updatePrefs()
        for (i in 1..1000) {
            Log.i("App", "onCreate: ")
            
            
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Loggy.log(e,true)
            defaultHandler.uncaughtException(t, e)
        }


    }
}