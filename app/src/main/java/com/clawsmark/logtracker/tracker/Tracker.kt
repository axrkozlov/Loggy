package com.clawsmark.logtracker.tracker

import com.clawsmark.logtracker.data.Level
import com.clawsmark.logtracker.data.TrackMessage
import com.clawsmark.logtracker.data.TrackBuffer
import com.clawsmark.logtracker.locator.LogTrackerServiceLocator

object Tracker {

    private val logDao = LogTrackerServiceLocator.LOG_FILE_DAO
    private val trackerPrefs = LogTrackerServiceLocator.trackerPrefs

    private val buffer = TrackBuffer()

    fun log(tag: String, message: String, level: Level){
        addMessage(TrackMessage(tag,message,level))
    }

    fun i(tag: String, message: String) {
        addMessage(TrackMessage(tag,message,Level.INFO))
    }

    fun w(tag: String, message: String) {
        addMessage(TrackMessage(tag,message,Level.WARNING))
    }

    fun e(tag: String, message: String) {
        addMessage(TrackMessage(tag,message,Level.ERROR))
    }

    private fun addMessage(trackMessage: TrackMessage){
        checkBufferSize()
        buffer.add(trackMessage)
    }

    private fun checkBufferSize() {

    }

    fun saveBuffer(){
        logDao.saveBuffer(buffer)
    }



}