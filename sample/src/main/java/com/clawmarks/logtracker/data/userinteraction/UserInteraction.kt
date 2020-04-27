package com.clawmarks.logtracker.data.userinteraction


interface UserInteraction {
    fun invoke()
    var listener: (() -> Unit)?
}