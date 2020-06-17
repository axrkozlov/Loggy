package com.clawmarks.loggy.userinteraction

import com.clawmarks.loggy.LoggyComponent

interface UserInteractionObserver: LoggyComponent {
    fun onInteraction()
    fun onIdle()
}