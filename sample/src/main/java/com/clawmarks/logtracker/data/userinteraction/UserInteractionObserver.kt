package com.clawmarks.logtracker.data.userinteraction

interface UserInteractionObserver {
    fun onInteraction()
    fun onIdle()
}