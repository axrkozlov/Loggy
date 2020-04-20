package com.clawsmark.logtracker.data.services.logsender

import android.util.Log
import com.clawsmark.logtracker.data.services.logfilemanager.LoggyFileList
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import kotlinx.coroutines.*
import java.io.File


class LoggySender(override val context: LoggyContext, private val analyticsFileList: LoggyFileList, val logcatFileList: LoggyFileList) : LoggyComponent {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private var sentLogNames = mutableListOf<String>()

    private var logsMustBeSent = listOf<File>()

    init {
        register()
    }

    fun markFileAsSent(file: File) {
        sentLogNames.add(file.nameWithoutExtension)
    }

    private fun updateLogFiles() {
        val allLogs = mutableListOf<File>()
        allLogs.addAll(analyticsFileList.list)
//        allLogs.addAll(logcatFileList.list)
        allLogs.sortBy { it.lastModified() }
        logsMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }
        val allLogsNames = allLogs.map { file -> file.nameWithoutExtension }
//        sentLogNames.retainAll { allLogsNames.contains(it) }
        Log.i("LogSender", "allLogs: $allLogs")
        Log.i("LogSender", "logsMustBeSent: $logsMustBeSent")
        Log.i("LogSender", "sentLogNames: $sentLogNames")
    }

    var isRunning = true
        private set
    var job: Job? = null

    fun startSending() {
        job = coroutineScope.launch {
            isRunning = true
            while (isRunning) {
                delay(1000)
                send()
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
            LogUploader().uploadSingleFile(file) {
                Log.i("LogSender", "sendFile: $it")
                markFileAsSent(file)
            }

        }
    }

    override fun onPrefsUpdated() {
        updateLogFiles()
    }
}