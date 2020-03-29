package com.clawsmark.logtracker.filemanager

import android.util.Log
import com.clawsmark.logtracker.data.Message
import com.clawsmark.logtracker.dispatcher.Event
import com.clawsmark.logtracker.dispatcher.Subscriber
import com.clawsmark.logtracker.dispatcher.TrackerDispatcher
import java.io.File

object FileManager {

    init {
        TrackerDispatcher.subscribe(object : Subscriber<Event.BufferSaveEvent> {

            override fun onNewEvent(event: Event) {
                Log.i("FileManager", "onNewEvent: ")
            }

        })
    }
//
//    val onBufferSaveEvent: ((LinkedHashSet<Message>) -> Unit) = {
//        onSaveFile(it)
//    }
//
//    fun getEldestFile(): File {
//        return File("")
//    }
//
//    val onDeleteFileEvent: ((File) -> Unit) = {
//        onDeleteFile(it)
//    }
//
//    private fun onSaveFile(buffer: LinkedHashSet<Message>) {
//        //save logic
//    }
//
//    private fun onDeleteFile(file: File) {
//        //save logic
//    }





}