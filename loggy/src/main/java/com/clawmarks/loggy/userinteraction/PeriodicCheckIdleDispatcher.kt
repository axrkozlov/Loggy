package com.clawmarks.loggy.userinteraction

import android.os.Handler
import android.os.Looper
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext

class PeriodicCheckIdleDispatcher(override val context: LoggyContext) : UserInteractionDispatcher(),LoggyContextComponent {
    private var timeForIsIdle:Long = 10 * 3600
    private var period: Long = 1000

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
        periodicCheckRunnable.invoke()
    }

    private fun checkIsIdle() {
        if (!isIdle && currentMillis - lastInteractionTime > timeForIsIdle) {
            isIdle = true
            dispatchIdle()
        }
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
        observers.forEach {
            it.onIdle()
        }
    }

    override fun onPrefsUpdated() {
        timeForIsIdle = prefs.timeForIsIdleMin * 60000
    }
}