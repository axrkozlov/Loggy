package com.clawmarks.logtracker

import android.app.Application
import com.clawmarks.loggy.Loggy
import com.clawmarks.loggy.di.appyModule
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.uploader.LoggyUploader
import com.clawmarks.loggy.utils.loggy
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
        val loggyUploader = get<LoggyUploader>()
        val loggyPrefs = get<LoggyPrefs>()

        Loggy.start(
//                LoggyUploaderImpl(),
                loggyUploader,
                loggyPrefs
        )

        loggy(tag, "onCreate")

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Loggy.dump(e,true)
            defaultHandler.uncaughtException(t, e)
        }


    }
}