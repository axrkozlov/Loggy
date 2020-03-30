package com.clawsmark.logtracker.data.services.logfilemanager

import com.clawsmark.logtracker.data.Buffer

interface LogFileDao {

    val trackerLogs: LogFileList
    val logcatLogs: LogFileList
    fun saveBuffer(buffer: Buffer<*>)

}