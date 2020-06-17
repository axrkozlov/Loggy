package com.clawmarks.loggy

import com.clawmarks.loggy.context.LoggyContext

interface LoggyContextComponent :LoggyComponent {

    val context: LoggyContext

    fun onPrefsUpdated()

    val prefs get() = context.prefs
    fun register(){
        context.register(this)
    }

}