package com.clawmarks.loggy.userinteraction

import android.os.Handler
import android.os.Looper
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext

class SendingPermissionDispatcherImpl(override val context: LoggyContext) :SendingPermissionDispatcher(),LoggyContextComponent {

    private var timeIsPermitted = 0L
    private val observers = mutableSetOf<SendingPermissionObserver>()
    private val handler = Handler(Looper.getMainLooper())
    private val permissionTimeElapsedRunnable = Runnable {onProhibited()}
    override fun onPermitted() {
        dispatchPermitted()
        planNewPermissionTimeWatcher()
     }

    override fun onProhibited() {
        dispatchProhibited()
    }

    private fun planNewPermissionTimeWatcher(){
        if (timeIsPermitted>0) {
            handler.removeCallbacksAndMessages(permissionTimeElapsedRunnable)
            handler.postDelayed(permissionTimeElapsedRunnable,timeIsPermitted)
        }
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