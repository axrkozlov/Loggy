package com.clawmarks.loggy.userinteraction


interface UserInteraction {
    fun invoke()
    var listener: (() -> Unit)?
}