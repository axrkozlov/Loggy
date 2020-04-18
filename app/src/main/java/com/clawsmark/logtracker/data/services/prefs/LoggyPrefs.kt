package com.clawsmark.logtracker.data.services.prefs

import java.io.File
import java.text.Format

interface LoggyPrefs {

    val bufferBlockSize: Int
    val maxBufferSize : Int

    val timeBeforeStartSendingSeconds : Int
    val minAvialableMemoryMb : Int

    val logcatBufferSizeKb:Int
    val logcatMinBufferSizeKb:Int
    val logcatMaxBufferSizeKb:Int

    val maxFileSizeKb :Int
    val maxFileSizeBytes :Int
    get ()=maxFileSizeKb*1024

    val serialNumber:String
    val terminalId:String

    val logcatPath:String
    val analyticsPath:String
    val analyticsFileNameFormat:String
    val logcatFileNameFormat:String


    val loggyPath: String
}