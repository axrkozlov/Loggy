package com.clawsmark.logtracker.data.userinteraction

import android.os.Handler
import android.os.Looper

class PeriodicCheckIdleDispatcher(private val period: Long = 60000, private val timeForIsIdle:Long = 60000) : UserInteractionDispatcher() {
    var isIdle = false
        private set
    private var lastInteractionTime: Long = currentMillis
    private val currentMillis: Long
        get() = System.currentTimeMillis()

    private val periodicCheckRunnable: () -> Unit
        get() = {
            checkIsIdle()
            handler.postDelayed(periodicCheckRunnable, period)
        }
    private val handler = Handler(Looper.getMainLooper())

    init {
        onInteraction()
        periodicCheckRunnable
    }

    private fun checkIsIdle() {
        if (!isIdle && currentMillis - lastInteractionTime > timeForIsIdle) dispatchIdle()
    }

    private val observers = mutableSetOf<UserInteractionObserver>()

    override fun onInteraction() {
        lastInteractionTime = currentMillis
        if (isIdle) dispatchInteraction()
        isIdle = false
    }

    override fun addObserver(userInteractionObserver: UserInteractionObserver) {
        observers.add(userInteractionObserver)
    }

    override fun removeObserver(userInteractionObserver: UserInteractionObserver) {
        observers.add(userInteractionObserver)
    }

    override fun dispatchInteraction() {
        observers.forEach {
            it.onInteraction()
        }
    }

    override fun dispatchIdle() {
        isIdle = true
        observers.forEach {
            it.onIdle()
        }
    }
}