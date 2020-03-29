package com.clawsmark.logtracker.tracker

import com.clawsmark.logtracker.courier.Courier
import com.clawsmark.logtracker.data.Message
import com.clawsmark.logtracker.data.MessageBuffer
import com.clawsmark.logtracker.dispatcher.Event
import com.clawsmark.logtracker.dispatcher.TrackerDispatcher
import com.clawsmark.logtracker.filemanager.FileManager

object Tracker {
//
//    var typingCallback : ((Double) -> Unit)?=null
//
//    private val bufferSaveDelegate: ((LinkedHashSet<Message>) -> Unit) by TrackerDispatcher
    init {
        TrackerDispatcher
        FileManager
    Courier
    }

    init {

    }

    private val buffer = MessageBuffer()

    fun i(tag: String, message: String) {

    }

    fun w(tag: String, message: String) {

    }

    fun e(tag: String, message: String) {

    }

    fun saveBuffer(){
//        bufferSaveDelegate.invoke(buffer)
        TrackerDispatcher.dispatch(Event.BufferSaveEvent(buffer))
    }

    fun saveBuffer1() {
        TrackerDispatcher.dispatch(Event.Simple())
    }


}