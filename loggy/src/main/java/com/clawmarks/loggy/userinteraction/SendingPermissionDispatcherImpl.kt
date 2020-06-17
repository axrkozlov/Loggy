package com.clawmarks.loggy.userinteraction

import android.os.Handler
import android.os.Looper
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext

class SendingPermissionDispatcherImpl(override val context: LoggyContext) :SendingPermissionDispatcher(),LoggyContextComponent {

    var timeIsPermitted = 0L
    private val observers = mutableSetOf<SendingPermissionObserver>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onPermitted() {
        dispatchPermitted()
        if (timeIsPermitted>0) handler.postDelayed({onProhibited()},timeIsPermitted)
     }

    override fun onProhibited() {
        dispatchProhibited()
    }

    override fun addObserver(sendingPermissionObserver: SendingPermissionObserver) {
         observers.add(sendingPermissionObserver)
     }

     override fun removeObserver(sendingPermissionObserver: SendingPermissionObserver) {
         observers.add(sendingPermissionObserver)
     }

     override fun dispatchPermitted() {
         context.hasSendingPermission = true
         observers.forEach {
             it.onPermitted()
         }
     }

     override fun dispatchProhibited() {
         observers.forEach {
             it.onPermitted()
         }
     }

    override fun onPrefsUpdated() {
        timeIsPermitted = prefs.timeIsSendingPermittedMin * 60000
    }

 }