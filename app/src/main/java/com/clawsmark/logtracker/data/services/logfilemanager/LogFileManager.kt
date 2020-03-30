package com.clawsmark.logtracker.data.services.logfilemanager

import android.os.Environment
import com.clawsmark.logtracker.data.Buffer
import com.clawsmark.logtracker.data.LogcatBuffer
import com.clawsmark.logtracker.data.TrackerBuffer
import com.clawsmark.logtracker.locator.LogTrackerServiceLocator
import java.io.*
import java.lang.Exception

object LogFileManager : LogFileDao {

    init {

    }


    private val maxBufferStringCount = 1_00
    private val prefs = LogTrackerServiceLocator.trackerPrefs


//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = job + Dispatchers.IO

    private val sdCard: File = Environment.getExternalStorageDirectory()
    private val trackerLogDir = File(sdCard.absolutePath.toString() + "/logs")
    private val logcatLogDir = File(sdCard.absolutePath.toString() + "/logcat")

    override val trackerLogs = LogFileList(trackerLogDir, 16)
    override val logcatLogs = LogFileList(logcatLogDir, 16)

    private val trackerWriter: BufferWritable = BufferWriter(trackerLogDir) {
        this.trackerLogs.update()
    }

    private val logcatWriter: BufferWritable = BufferWriter(logcatLogDir) {
        this.logcatLogs.update()
    }

    override fun saveBuffer(buffer: Buffer<*>) {
        when (buffer) {
            is LogcatBuffer -> logcatWriter.write(buffer)
            is TrackerBuffer -> trackerWriter.write(buffer)
        }

    }


}