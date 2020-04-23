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


class LoggySender(override val context: LoggyContext, private val loggyUploader: LoggyUploader, private val analyticsFileList: LoggyFileList, private val logcatFileList: LoggyFileList) : LoggyComponent {

    private var sentLogNames = mutableListOf<String>()
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
    private var isSendingDeferred = false
    private var isSendingUrgent = false

    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var sendingJob: Job? = null
    private val completionHandler: CompletionHandler = { onCompleteSending(it) }

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
        sendErrorLogNames.clear()
        return listMustBeSent
    }


    fun startSending(isUrgentlySendingRequest: Boolean = false) {
        if (isUrgentlySendingRequest) {
            isUrgentlySendingRequested = true
            Log.i("LoggySender", "startSending: urgently sending started")
        }
        isActive = true
        sendingJob=sendFiles()
        sendingJob?.invokeOnCompletion(completionHandler)
    }

    var isUrgentlySendingRequested = false


    fun stopSending(isForce: Boolean) {
        if ((isUrgentlySendingRequested || isSendingUrgent) && !isForce) {
            Log.i("LoggySender", "stopSending: sending can't be stopped cause it's urgent")
            return
        }
        isActive = false
        sendingJob?.cancel()
    }

    private fun sendFiles() = coroutineScope.launch {
        coroutineScope {
            isSendingInProgress = true
            val list = getSendingFileList()
            sentCount = 0
            while (isActive && list.isNotEmpty()) {
                sendFile(list[0])
                list.removeAt(0)
                delay(1000)
            }
            isSendingInProgress = false
        }
    }

    private suspend fun sendFile(file: File) {
        coroutineScope {
            val name = file.nameWithoutExtension
            Log.i("LoggySender", "sendFile:${file.nameWithoutExtension}")
            loggyUploader.uploadSingleFile(file) { success ->
                if (success) {
                    Log.i("LoggySender", "sendFile: File $name has been sent")
                    sentCount += 1
                    sentLogNames.add(name)
                } else {
                    sendErrorLogNames.add(name)
                }
            }
        }
    }

    private fun onCompleteSending(throwable: Throwable?) {
        isSendingInProgress = false
        isUrgentlySendingRequested = false
        val resultText = when {
            throwable != null -> "with error : $throwable"
            sendErrorLogNames.isNotEmpty() -> "with error on files $sendErrorLogNames"
            else -> "successfully"
        }
        Log.i("LoggySender", "onCompleteSending: Sending completed $resultText. Sent $sentCount file(s).")
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