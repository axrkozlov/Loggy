package com.clawsmark.logtracker.data.services.logsender

import android.util.Log
import com.clawsmark.logtracker.data.services.logfilemanager.LoggyFileList
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import kotlinx.coroutines.*
import java.io.File


class LoggySender(override val context: LoggyContext, private val analyticsFileList: LoggyFileList, val logcatFileList: LoggyFileList) : LoggyComponent {

    private var sentLogNames = mutableListOf<String>()

    init {
        register()
    }

    private fun getSendingFileList():MutableList<File> {
        val allLogs = mutableListOf<File>()
        allLogs.addAll(analyticsFileList.list)
        allLogs.addAll(logcatFileList.list)
        allLogs.sortBy { it.lastModified() }
        val allLogsNames = allLogs.map { file -> file.nameWithoutExtension }
        val listMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }.toMutableList()
        sentLogNames.retainAll { allLogsNames.contains(it) }
        return listMustBeSent
//        Log.i("LogSender", "allLogs: $allLogs")
//        Log.i("LogSender", "logsMustBeSent: $logsMustBeSent")
//        Log.i("LogSender", "sentLogNames: $sentLogNames")
    }

    var isRunning = true
        private set
    var job: Job? = null

    fun startSending() {
        val list = getSendingFileList()
        job = CoroutineScope(Dispatchers.IO + Job()).launch {
            isRunning = true
            while (isRunning && list.isNotEmpty()) {
                send(list[0])
                list.removeAt(0)
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