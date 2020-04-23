package com.clawsmark.logtracker.data.userinteraction

class UserInteractionImpl() : UserInteraction {

    override fun invoke() {
        listener?.invoke()
    }

    override var listener: (() -> Unit)? = null

}