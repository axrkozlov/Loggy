package com.clawsmark.logtracker.data.services.logmanager

import com.clawsmark.logtracker.data.LogcatBuffer
import com.clawsmark.logtracker.data.TrackBuffer
import java.io.File

interface LogFileDao {
    val fileToBeSent: File?
    fun fileHasBeenSent(fileName : String)
    fun saveBuffer(trackBuffer: TrackBuffer)
    fun saveBuffer(logcatBuffer: LogcatBuffer)

}