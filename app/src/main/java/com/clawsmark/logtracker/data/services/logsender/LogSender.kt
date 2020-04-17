package com.clawsmark.logtracker.data.services.logsender

import android.util.Log
import com.clawsmark.logtracker.data.services.logfilemanager.LogFileDir
import kotlinx.coroutines.*
import java.io.File


class LogSender(val analytiscDir:LogFileDir) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())


    private var sentLogNames = mutableListOf<String>()

    private var logsMustBeSent = listOf<File>()

    init {
        coroutineScope.launch {
            updateLogFiles()
        }
    }

    fun markFileAsSent(file: File) {
        coroutineScope.launch {
            sentLogNames.add(file.nameWithoutExtension)
            updateLogFiles()
        }
    }

    private suspend fun updateLogFiles() {
        coroutineScope {
            delay(1000)
            val allLogs = analytiscDir.list
            logsMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }
            val allLogsNames = allLogs.map { file -> file.nameWithoutExtension }
            sentLogNames.retainAll { allLogsNames.contains(it) }
        }
    }

    var isRunning = true
        private set
var job:Job?=null
    fun startSending() {
        job=coroutineScope.launch {
            launch {
                isRunning = true
                while (isRunning) {
                    delay(1000)
                    send()
                }
            }
        }
    }

    fun stopSending() {
        isRunning = false
        //cancel call
        job?.cancel()
    }

    suspend fun send() {
        coroutineScope {
            updateLogFiles()
            if (logsMustBeSent.isNotEmpty()) {
                sendFile(logsMustBeSent[0])
            } else isRunning = false
        }
    }


    suspend fun sendFile(file: File) {
        coroutineScope {
//            api.send(file)
            Log.i("LogSender", "sendFile:${file.nameWithoutExtension}")
            LogUploader().uploadSingleFile(file){
                Log.i("LogSender", "sendFile: $it")
                markFileAsSent(file)
            }

        }
    }


}