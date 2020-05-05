package com.clawmarks.loggy.sender

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.clawmarks.loggy.Loggy
import com.clawmarks.loggy.filelist.LoggyFileList
import com.clawmarks.loggy.LoggyComponent
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.uploader.LoggyUploader
import kotlinx.coroutines.*
import java.io.File
import java.lang.Runnable
import java.util.Observer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet


class LoggySender(
        override val context: LoggyContext,
        private val sentNamesHolder: SentNamesHolder,
        private val analyticsFileList: LoggyFileList,
        private val logcatFileList: LoggyFileList,
        var loggyUploader: LoggyUploader) : LoggyComponent {

    private val sendingFiles = CopyOnWriteArrayList<File>()
    private var sentLogNames = CopyOnWriteArraySet<String>()
    private var sendErrorLogNames = mutableListOf<String>()
    var sentCount = 0

    var isActive = false
        private set
    var isSendingInProgress = false
        private set

    private var sendingInterval: Long = 0
    private val isSendingWhenFileListUpdated: Boolean
        get() = sendingInterval <= 0
    private var pauseBetweenFileSending: Long = 1000
    private var isSendingUrgent = false

    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var sendingJob: Job? = null

    init {
        loadSentNames()
        register()
    }

    private fun updateSendingFileList() {
        val allLogs = mutableListOf<File>()
        allLogs.addAll(analyticsFileList.list)
        allLogs.addAll(logcatFileList.list)
        allLogs.sortBy { it.lastModified() }
        val allLogsNames = allLogs.map { file -> file.nameWithoutExtension }
        val listMustBeSent = allLogs.filter { !sentLogNames.contains(it.nameWithoutExtension) }.toMutableList()
        sendingFiles.retainAll(listMustBeSent)
        sendingFiles.addAllAbsent(listMustBeSent)
        sentLogNames.forEach { if (!allLogsNames.contains(it)) sentLogNames.remove(it) }
    }


    fun startSending(isUrgentlySendingRequest: Boolean = false) {
        if (isUrgentlySendingRequest) {
            isUrgentlySendingRequested = true
            Log.i("LoggySender", "startSending: urgently")
        }
        Log.i("LoggySender", "startSending:")
        isActive = true
        sendFiles()
    }

    private var isUrgentlySendingRequested = false

    fun stopSending(isForce: Boolean) {
        if ((isUrgentlySendingRequested || isSendingUrgent) && !isForce) {
            Log.i("LoggySender", "stopSending: sending can't be stopped cause it's urgent")
            return
        }
        Log.i("LoggySender", "stopSending: ")
        isActive = false
        loggyUploader.cancel()
        sendingJob?.cancel()
    }

    private fun sendFiles() {
        if (sendingJob != null) return
        sendingJob = coroutineScope.launch {
            updateSendingFileList()
            sendErrorLogNames.clear()
            isSendingInProgress = true
            sentCount = 0
            while (isActive && sendingFiles.isNotEmpty()) {
                sendFile(sendingFiles[0])
                sendingFiles.removeAt(0)
                delay(pauseBetweenFileSending)
            }
            isSendingInProgress = false

        }
        sendingJob!!.invokeOnCompletion { onCompleteSending(it) }
    }

    private fun sendFile(file: File) {
        val name = file.nameWithoutExtension
        Log.i("LoggySender", "sendFile:${file.nameWithoutExtension}")
        val sendingSuccess = loggyUploader.uploadSingleFile(file)
        if (sendingSuccess) {
            Log.i("LoggySender", "sendFile: File $name has been sent")
            sentCount += 1
            sentLogNames.add(name)
        } else {
            sendErrorLogNames.add(name)
        }
    }

    private fun onCompleteSending(throwable: Throwable?) {

        val resultText = when {
            sendingJob?.isCancelled == true -> "cancelled"
            throwable != null -> "completed with error : $throwable"
            sendErrorLogNames.isNotEmpty() -> "completed with error on files $sendErrorLogNames"
            else -> "successfully"
        }
        Log.i("LoggySender", "onCompleteSending: Sending $resultText. Sent $sentCount file(s).")
        saveSentNames()
        sendingJob = null
        isSendingInProgress = false
        isUrgentlySendingRequested = false
    }

    private fun saveSentNames() {
        sentNamesHolder.save(sentLogNames)
    }

    private fun loadSentNames() {
        sentLogNames = sentNamesHolder.load()
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
    private val sendingPeriodicRunnable: Runnable = Runnable {
        if (sendingInterval > 0) {
            this@LoggySender.addRunnable()
            updateSendingFileList()
            if (isSendingInProgress || !isActive) return@Runnable
            Log.i("LoggySender", "LoggySender: start sending on interval")
            sendFiles()
        }
    }

    private fun setupUpdateIntervalHandler() {
        handler.removeCallbacksAndMessages(sendingPeriodicRunnable)
        addRunnable()
    }

    private fun addRunnable() {
        handler.postDelayed(sendingPeriodicRunnable, sendingInterval)
    }

    private val listUpdateObserver by lazy {
        Observer { _, _ ->
            updateSendingFileList()
            if (isSendingInProgress || !isActive) return@Observer
            Log.i("LoggySender", "LoggySender: start sending when a file list updated")
            sendFiles()
        }
    }

    private fun setupUpdateObserver() {
        if (isSendingWhenFileListUpdated) {
            analyticsFileList.subscribeUpdates(listUpdateObserver)
            logcatFileList.subscribeUpdates(listUpdateObserver)
        } else {
            analyticsFileList.unsubscribeUpdates(listUpdateObserver)
            logcatFileList.unsubscribeUpdates(listUpdateObserver)
        }
    }


}