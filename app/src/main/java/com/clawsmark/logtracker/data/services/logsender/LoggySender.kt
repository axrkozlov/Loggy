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
    private var listMustBeSent = mutableListOf<File>()

    init {
        register()
    }

    private fun updateSendingFileList() {
        val allLogs = mutableListOf<File>()
        allLogs.addAll(analyticsFileList.list)
        allLogs.addAll(logcatFileList.list)
        allLogs.sortBy { it.lastModified() }
        listMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }.toMutableList()
        val allLogsNames = allLogs.map { file -> file.nameWithoutExtension }
        sentLogNames.retainAll { allLogsNames.contains(it) }
//        Log.i("LogSender", "allLogs: $allLogs")
//        Log.i("LogSender", "logsMustBeSent: $logsMustBeSent")
//        Log.i("LogSender", "sentLogNames: $sentLogNames")
    }

    var isRunning = true
        private set
    var job: Job? = null

    fun startSending() {
        updateSendingFileList()
        job = coroutineScope.launch {
            isRunning = true
            while (isRunning && listMustBeSent.isNotEmpty()) {
                send(listMustBeSent[0])
                listMustBeSent.removeAt(0)
            }
        }
    }

    fun stopSending() {
        isRunning = false
        //cancel call
        job?.cancel()
    }

    private suspend fun send(file: File) {
        coroutineScope {
                Log.i("LogSender", "sendFile:${file.nameWithoutExtension}")
                LoggyUploader().uploadSingleFile(file) {
                    if (it) markFileAsSent(file)
                }
                delay(1000)
        }
    }

    private fun markFileAsSent(file: File) {
        sentLogNames.add(file.nameWithoutExtension)
    }

    override fun onPrefsUpdated() {

    }
}