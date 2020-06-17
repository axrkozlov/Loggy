package com.clawmarks.loggy.userinteraction

import com.clawmarks.loggy.LoggyComponent

abstract class UserInteractionDispatcher:LoggyComponent {
    abstract fun onInteraction()

    abstract fun addObserver(userInteractionObserver: UserInteractionObserver)
    abstract fun removeObserver(userInteractionObserver: UserInteractionObserver)

    protected abstract fun dispatchInteraction()
    protected abstract fun dispatchIdle()

}