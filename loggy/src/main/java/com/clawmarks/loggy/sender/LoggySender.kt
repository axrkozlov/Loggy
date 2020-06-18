package com.clawmarks.loggy.sender

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.clawmarks.loggy.filelist.LoggyFileList
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.uploader.LoggyUploader
import com.clawmarks.loggy.uploader.UploadResult
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception
import java.lang.Runnable
import java.util.Observer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet


class LoggySender(
        override val context: LoggyContext,
        private val sentNamesHolder: SentNamesHolder,
        private val analyticsFileList: LoggyFileList,
        private val logcatFileList: LoggyFileList,
        var loggyUploader: LoggyUploader) : LoggyContextComponent {

    private val sendingFiles = CopyOnWriteArrayList<File>()
    private var sentLogNames = CopyOnWriteArraySet<String>()
    private var sendErrorLogNames = mutableListOf<String>()
    private var sentCount = 0

    private var isSendingActive = false
    var isSendingInProgress = false
        private set

    private var sendingInterval: Long = 0
    private var sendingRetryInterval: Long = 0

    private val isSendingWhenFileListUpdated: Boolean
        get() = sendingInterval <= 0
    private var pauseBetweenFileSending: Long = 1000
    private var isSendingUrgent = false

    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var sendingJob: Job? = null

    var hasUploaderError = false
    var hasApiError = false

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
        isSendingActive = true
        sendFiles()
    }

    private var isUrgentlySendingRequested = false

    fun stopSending(isForce: Boolean) {
        if ((isUrgentlySendingRequested || isSendingUrgent) && !isForce) {
            Log.i("LoggySender", "stopSending: sending can't be stopped cause it's urgent")
            return
        }
        Log.i("LoggySender", "stopSending: ")
        isSendingActive = false
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
            while (isSendingActive && sendingFiles.isNotEmpty()) {
                val canProceed = sendFile(sendingFiles[0])
                if (!canProceed) break
                sendingFiles.removeAt(0)
                delay(pauseBetweenFileSending)
            }
            isSendingInProgress = false
        }
        sendingJob!!.invokeOnCompletion { onCompleteSending(it) }
    }


    private fun sendFile(file: File): Boolean {
        if (!file.exists()) {
            Log.e("LoggySender", "sendFile: $file does not exist anymore")
            return true
        }
        Log.i("LoggySender", "sendFile:${file.nameWithoutExtension}")
        val uploadResult = try {
            loggyUploader.uploadSingleFile(file)
        } catch (e: Exception) {
            Log.e("LoggySender", "Uploading file uploader error", e)
            UploadResult.UploaderError
        }
        return processUploadResult(file, uploadResult)
    }

    private fun processUploadResult(file: File, uploadResult: UploadResult): Boolean {
        val name = file.nameWithoutExtension
        Log.i("LoggySender", "processUploadResult: File = $name, uploadResult = $uploadResult")
        when (uploadResult) {
            UploadResult.Success -> {
                Log.i("LoggySender", "processUploadResult: File $name has been sent")
                sentCount += 1
                sentLogNames.add(name)
                hasUploaderError = false
                hasApiError = false
                return true
            }
            UploadResult.UploaderError -> {
                if (!hasUploaderError) {
                    Log.e("LoggySender", "processUploadResult: Trying to resend once on uploader error")
                    hasUploaderError = true
                    sendFile(file)
                } else {
                    sendErrorLogNames.add(name)
                }
            }
            UploadResult.UploadApiError -> {
                if (!hasApiError) {
                    Log.e("LoggySender", "processUploadResult: Plan resend on api error")
                    hasApiError = true
                    planNextSendingTask(true)
                    sendErrorLogNames.add(name)
                } else {
                    sendErrorLogNames.add(name)
                }
            }
            UploadResult.CorruptedFileError -> {
                Log.e("LoggySender", "processUploadResult: File $name is corrupted and will be deleted")
                try {
                    file.delete()
                } catch (e: Exception) {
                    Log.e("LoggySender", "processUploadResult: File $name is corrupted and can't be deleted")
                }
            }
        }
        return false
    }

    private fun onCompleteSending(throwable: Throwable?) {
        val lastErrorFileName = if (sendErrorLogNames.isNotEmpty()) sendErrorLogNames.last() else ""
        val resultText = when {
            sendingJob?.isCancelled == true -> "cancelled"
            throwable != null -> "completed with error : $throwable"
            hasUploaderError -> "completed with uploader error on file: $lastErrorFileName"
            hasApiError -> "completed with api error on file: $lastErrorFileName"
            sendErrorLogNames.isNotEmpty() -> "completed with error on files $sendErrorLogNames"
            else -> "successfully"
        }
        Log.i("LoggySender", "onCompleteSending: Sending $resultText. Sent $sentCount file(s).")
        saveSentNames()
        if (throwable != null) planNextSendingTask(true)
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
        if (context.isSendingUrgent) isSendingActive = true
        sendingInterval = prefs.sendingIntervalMin * 60 * 1000
        if (sendingInterval <= 0) sendingRetryInterval = prefs.sendingRetryIntervalMin * 60 * 1000
        pauseBetweenFileSending = prefs.pauseBetweenFileSendingSec * 1000
        setupUpdateIntervalHandler()
        setupUpdateObserver()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val sendingPeriodicRunnable: Runnable = Runnable {
        updateSendingFileList()
        if (isSendingInProgress || !isSendingActive) return@Runnable
        Log.i("LoggySender", "Start sending after interval is elapsed")
        sendFiles()
    }

    private fun setupUpdateIntervalHandler() {
        clearSendingTask()
        planNextSendingTask()
    }

    private fun planNextSendingTask(retryOnError: Boolean = false) {
        if (sendingInterval > 0) {
            Log.i("LoggySender", "Sending task planned in $sendingInterval")
            handler.postDelayed(sendingPeriodicRunnable, sendingInterval)
        } else if (retryOnError) {
            Log.i("LoggySender", "Sending task on error occurred planned in $sendingRetryInterval")
            handler.postDelayed(sendingPeriodicRunnable, sendingRetryInterval)
        }
    }

    private fun clearSendingTask() {
        handler.removeCallbacksAndMessages(sendingPeriodicRunnable)
    }

    private val listUpdateObserver by lazy {
        Observer { _, _ ->
            clearSendingTask()
            updateSendingFileList()
            if (isSendingInProgress || !isSendingActive) return@Observer
            Log.i("LoggySender", "Start sending when a file list updated")
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