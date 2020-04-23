package com.clawsmark.logtracker.data.sender

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.clawsmark.logtracker.data.filelist.LoggyFileList
import com.clawsmark.logtracker.data.LoggyComponent
import com.clawsmark.logtracker.data.context.LoggyContext
import kotlinx.coroutines.*
import java.io.File
import java.lang.Runnable
import java.util.Observer


class LoggySender(override val context: LoggyContext, private val analyticsFileList: LoggyFileList, private val logcatFileList: LoggyFileList) : LoggyComponent {

    private var sentLogNames = mutableListOf<String>()

    var isActive = false
        private set
    var isSendingInProgress = false
        private set

    private var sendingInterval: Long = 0
    private val isSendingByFileUpdated: Boolean
        get() = sendingInterval > 0
    private var pauseBetweenFileSending: Long = 1000
    private var isSendingDeferred = false
    private var isSendingUrgent = false


    init {
        register()
    }

    private fun getSendingFileList(): MutableList<File> {
        val allLogs = mutableListOf<File>()
        allLogs.addAll(analyticsFileList.list)
        allLogs.addAll(logcatFileList.list)
        allLogs.sortBy { it.lastModified() }
        val allLogsNames = allLogs.map { file -> file.nameWithoutExtension }
        val listMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }.toMutableList()
        sentLogNames.retainAll { allLogsNames.contains(it) }
        return listMustBeSent
    }

    var job: Job = CoroutineScope(Dispatchers.IO + Job()).launch {
        isSendingInProgress = true
        val list = getSendingFileList()
        while (isActive && list.isNotEmpty()) {
            send(list[0])
            list.removeAt(0)
            delay(pauseBetweenFileSending)
        }
        isSendingInProgress = false
    }

    fun startSending(isUrgentlySendingRequest: Boolean = false) {
        if (isUrgentlySendingRequest) {
            isUrgentlySendingRequested = true
            Log.i("LoggySender", "startSending: urgently sending started")
        }

        isActive = true
        job.start()
        job.invokeOnCompletion {
            isSendingInProgress = false
            isUrgentlySendingRequested = false
            val resultText = if (it == null) "successfully" else "with error : $it"
            Log.i("LoggySender", "startSending: sending completed $resultText")
        }
    }

    var isUrgentlySendingRequested = false


    fun stopSending(isForce: Boolean) {
        if ((isUrgentlySendingRequested || isSendingUrgent) && !isForce) {
            Log.i("LoggySender", "stopSending: sending can't be stopped cause it's urgent")
            return
        }
        isActive = false
        job.cancel()
    }

    private suspend fun send(file: File) {
        coroutineScope {
            Log.i("LogSender", "sendFile:${file.nameWithoutExtension}")
            LoggyUploader().uploadSingleFile(file) {
                if (it) markFileAsSent(file)
            }
        }
    }

    private fun markFileAsSent(file: File) {
        sentLogNames.add(file.nameWithoutExtension)
    }

    override fun onPrefsUpdated() {
        if (!context.isSendingEnabled) {
            stopSending(true)
            sendingInterval = 0
            pauseBetweenFileSending = 0
        }
        if (context.isSendingUrgent) isActive = true
        sendingInterval = prefs.sendingIntervalMin * 60 * 1000
        pauseBetweenFileSending = prefs.pauseBetweenFileSendingSec * 1000
        setupUpdateIntervalHandler()
        setupUpdateObserver()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable: Runnable = Runnable {
        if (sendingInterval > 0) {
            this@LoggySender.addRunnable()
            if (isActive) {
                Log.i("LoggySender", "LoggySender: start sending on interval")
                startSending()
            }
        }
    }

    private fun setupUpdateIntervalHandler() {
        handler.removeCallbacksAndMessages(updateRunnable)
        addRunnable()
    }

    private fun addRunnable() {
        handler.postDelayed(updateRunnable, sendingInterval)
    }

    private val listUpdateObserver by lazy {
        Observer { _, _ ->
            startSending()
            Log.i("LoggySender", "LoggySender: start sending when a file list updated")

        }
    }

    private fun setupUpdateObserver() {
        if (isSendingByFileUpdated) {
            analyticsFileList.subscribeUpdates(listUpdateObserver)
            logcatFileList.subscribeUpdates(listUpdateObserver)
        } else {
            analyticsFileList.unsubscribeUpdates(listUpdateObserver)
            logcatFileList.unsubscribeUpdates(listUpdateObserver)
        }

    }


}