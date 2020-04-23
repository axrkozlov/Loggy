package com.clawsmark.logtracker.data.userinteraction

interface UserInteractionObserver {
    fun onInteraction()
    fun onIdle()
}