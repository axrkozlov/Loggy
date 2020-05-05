package com.clawmarks.logtracker

import android.app.Application
import android.util.Log
import com.clawmarks.loggy.userinteraction.UserInteractionDispatcher
import com.clawmarks.loggy.userinteraction.UserInteractionObserver
import com.clawmarks.loggy.Loggy
import com.clawmarks.loggy.di.appyModule
import com.clawmarks.logtracker.api.LoggyUploaderImpl
import com.clawmarks.logtracker.prefs.LoggyPrefsImpl
import com.clawmarks.logtracker.utils.trace
import org.koin.android.ext.android.get
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
            modules(listOf(appyModule))
        }

         Loggy.start(
                LoggyUploaderImpl(),
                 LoggyPrefsImpl()
        )

        trace(tag, "onCreate")

//        Loggy.context.logLevel = 3
        Loggy.updatePrefs()
//        for (i in 1..1000) {
//            Log.i("App", "onCreate: ")
//
//
//        }
        val userInteractionDispatcher = get<UserInteractionDispatcher>()
        userInteractionDispatcher.addObserver(object:UserInteractionObserver{
            override fun onInteraction() {
                Loggy.stopSending()
                Log.i("App", "onInteraction: Loggy.stopSending")

            }

            override fun onIdle() {
                Loggy.startSending()
                Log.i("App", "onIdle: Loggy.startSending")

            }

        })

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Loggy.dump(e,true)
            defaultHandler.uncaughtException(t, e)
        }


    }
}