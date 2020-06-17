package com.clawmarks.loggy.userinteraction

import com.clawmarks.loggy.LoggyComponent

interface SendingPermissionObserver: LoggyComponent {
    fun onPermitted()
    fun onProhibited()
}