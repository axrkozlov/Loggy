package com.clawsmark.logtracker.tracker

import com.clawsmark.logtracker.data.Level
import com.clawsmark.logtracker.data.TrackerMessage
import com.clawsmark.logtracker.data.TrackerBuffer
import com.clawsmark.logtracker.locator.LogTrackerServiceLocator

object Tracker {

    private val logDao = LogTrackerServiceLocator.LOG_FILE_DAO
    private val trackerPrefs = LogTrackerServiceLocator.trackerPrefs

    private val buffer = TrackerBuffer()

    fun log(tag: String, message: String, level: Level){
        addMessage(TrackerMessage(tag,message,level))
    }

    fun i(tag: String, message: String) {
        addMessage(TrackerMessage(tag,message,Level.INFO))
    }

    fun w(tag: String, message: String) {
        addMessage(TrackerMessage(tag,message,Level.WARNING))
    }

    fun e(tag: String, message: String) {
        addMessage(TrackerMessage(tag,message,Level.ERROR))
    }

    private fun addMessage(trackerMessage: TrackerMessage){
        checkBufferSize()
        buffer.add(trackerMessage)
    }

    private fun checkBufferSize() {

    }

    fun saveBuffer(){
        logDao.saveBuffer(buffer)
    }



}