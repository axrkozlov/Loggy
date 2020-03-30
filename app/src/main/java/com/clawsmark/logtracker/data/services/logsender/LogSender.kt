package com.clawsmark.logtracker.data.services.logsender

import android.util.Log
import com.clawsmark.logtracker.data.services.logfilemanager.LogFileDao
import com.clawsmark.logtracker.locator.LogTrackerServiceLocator
import kotlinx.coroutines.*
import java.io.File


object LogSender {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())


    private var sentLogNames = mutableListOf<String>()

    private val logFileDao: LogFileDao = LogTrackerServiceLocator.LogFileDao
    private val allLogs = logFileDao.trackerLogs
    private var logsMustBeSent = listOf<File>()

    init {
        coroutineScope.launch {
            updateLogFiles()
        }
    }

    fun markFileAsSent(fileName: String) {
        coroutineScope.launch {

//
            logsMustBeSent = allLogs
//            sentLogNames.add(logsMustBeSent[0].nameWithoutExtension)

            sentLogNames.add(logsMustBeSent[2].nameWithoutExtension)

            updateLogFiles()
        }
    }

    private suspend fun updateLogFiles() {
        coroutineScope {
            delay(1000)
            logsMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }
            val allFileNames = allLogs.map { file -> file.nameWithoutExtension }
            sentLogNames.retainAll { allFileNames.contains(it) }
            logsMustBeSent.forEach {
                Log.i("LogSender", "updateLogFiles: $it")
            }
            Log.i("LogSender", "___________________________________t")
        }
    }

}