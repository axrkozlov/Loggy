package com.clawsmark.logtracker.loggy

interface LoggyComponent {

    val context: LoggyContext
    val componentName: String
        get() = this::class.java.simpleName

    fun onPrefsUpdated()
    fun onFail() {
        context.onComponentFail(this)
    }

    val prefs get() = context.prefs
    fun register(){
        context.register(this)
    }

}