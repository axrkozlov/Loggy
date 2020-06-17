package com.clawmarks.loggy.userinteraction

import com.clawmarks.loggy.LoggyComponent

abstract class SendingPermissionDispatcher:LoggyComponent {
    abstract fun onPermitted()
    abstract fun onProhibited()


    abstract fun addObserver(sendingPermissionObserver: SendingPermissionObserver)
    abstract fun removeObserver(sendingPermissionObserver: SendingPermissionObserver)

    protected abstract fun dispatchPermitted()
    protected abstract fun dispatchProhibited()

}