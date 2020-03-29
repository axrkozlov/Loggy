package com.clawsmark.logtracker.courier

import android.util.Log
import com.clawsmark.logtracker.dispatcher.Event
import com.clawsmark.logtracker.dispatcher.Subscriber
import com.clawsmark.logtracker.dispatcher.TrackerDispatcher
import java.io.File

object Courier{

    init {
        TrackerDispatcher.subscribe(object : Subscriber<Event.Simple> {

            override fun onNewEvent(event: Event) {
                Log.i("Courier", "onNewEvent: ")

            }

        })



    }
//
//    private val eldestFile: File
//    get() = TrackerDispatcher.eldestFile
//
//    private val deleteSentFileDelegate: ((File) -> Unit) by TrackerDispatcher
//
//
//


}