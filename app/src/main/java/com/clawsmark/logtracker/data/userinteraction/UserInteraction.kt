package com.clawsmark.logtracker.data.userinteraction


interface UserInteraction {
    fun invoke()
    var listener: (() -> Unit)?
}