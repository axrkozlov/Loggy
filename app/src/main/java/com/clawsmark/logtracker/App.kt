package com.clawsmark.logtracker

import android.app.Application
import com.clawsmark.logtracker.tracker.Tracker
import com.clawsmark.logtracker.utils.trace

class App : Application() {
    val tag = "App"
    override fun onCreate() {
        super.onCreate()
        trace(tag, "onCreate")
        for (i in 1..100) {
            trace(tag, "message $i")
        }
        Tracker.saveBuffer()
    }
}