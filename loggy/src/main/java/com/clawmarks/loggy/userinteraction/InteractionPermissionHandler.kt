package com.clawmarks.loggy.userinteraction

import android.util.Log
import com.clawmarks.loggy.Loggy
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext

class InteractionPermissionHandler(override val context: LoggyContext) :UserInteractionObserver, SendingPermissionObserver, LoggyContextComponent {


    var isIdle = false
    var hasPermission =false

    override fun onInteraction() {
        Log.i("InteractionPermission", "onInteraction")
        isIdle = false
        stopSending()
    }

    override fun onIdle() {
        Log.i("InteractionPermission", "onIdle")
        isIdle = true
        startSending()
    }

    override fun onPermitted() {
        Log.i("InteractionPermission", "onPermitted")
        hasPermission = true
        startSending()
    }

    override fun onProhibited() {
        Log.i("InteractionPermission", "onProhibited")
        hasPermission = false
    }

    private fun startSending(){
        if (context.isSendingByServerRequest) return
        if (!isIdle || !hasPermission) return
        Log.i("InteractionPermission", "startSending")
        Loggy.startSending()

    }

    private fun stopSending(){
        if (context.isSendingByServerRequest) return
        Log.i("InteractionPermission", "stopSending")
        Loggy.stopSending()
    }

    override fun onPrefsUpdated() {
    }

}