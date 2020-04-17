package com.clawsmark.logtracker.useractivitywatcher

abstract class UserActivityDispatcher(val userActivityCallback: UserActivityCallback) {

    abstract fun dispatchInteraction()
    abstract fun dispatchIdle()

}