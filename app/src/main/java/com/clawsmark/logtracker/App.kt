package com.clawsmark.logtracker

import android.app.Application
import com.clawsmark.logtracker.di.appModule
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

    }
}