package com.clawmarks.logtracker.data

import com.clawmarks.logtracker.data.context.LoggyContext

interface LoggyComponent {

    val context: LoggyContext
    val componentName: String
        get() = this::class.java.simpleName

    fun onPrefsUpdated()

    val prefs get() = context.prefs
    fun register(){
        context.register(this)
    }

}