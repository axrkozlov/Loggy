package com.clawsmark.logtracker.data.services.prefs

import java.text.Format

interface LoggyPrefs {

    val bufferBlockSize: Int
    val maxBufferSize : Int

    val timeBeforeStartSendingSeconds : Int
    val maxLogLinesInFile : Int
    val minAvialableMemoryMb : Int

    val logcatBufferSizeKb:Int
    val logcatMaxBufferSizeKb:Int


    val serialNumber:String
    val logcatPath:String
    val analyticsPath:String
    val analyticsFileNameFormat:String
    val logcatFileNameFormat:String




}