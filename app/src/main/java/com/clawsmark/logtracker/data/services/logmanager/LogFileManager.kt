package com.clawsmark.logtracker.data.services.logmanager

import android.os.Environment
import android.util.Log
import com.clawsmark.logtracker.data.LogcatBuffer
import com.clawsmark.logtracker.data.TrackBuffer
import com.clawsmark.logtracker.data.services.TrackFiles
import com.clawsmark.logtracker.locator.LogTrackerServiceLocator
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashSet

object LogFileManager : LogFileDao {



    private val maxBufferStringCount = 1_00
    private val prefs= LogTrackerServiceLocator.trackerPrefs


//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = job + Dispatchers.IO

    var isRunning = true
        private set

    private val sdCard: File = Environment.getExternalStorageDirectory()
    private val dir = File(sdCard.absolutePath.toString() + "/logs")

    init {

        try {
           if (!dir.exists()) dir.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun writeLogBufferToFile(trackBuffer: TrackBuffer){
        try {
            val format = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
            val humanDate = format.format(Date(System.currentTimeMillis()))
            val logFile = File(dir, "Log_$humanDate.txt")

            val fOut = FileOutputStream(logFile)
            val osw = OutputStreamWriter(fOut)
            for (item in trackBuffer) {
                osw.write("$item\n")
            }
            osw.flush()
            osw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }










    private val trackFiles = TrackFiles(dir,16)


    private val logcatFiles = LinkedHashSet<File>()
    private val filesWasSent = LinkedHashSet<String>()




    override val fileToBeSent: File?
        get() = trackFiles.eldestFile

    override fun fileHasBeenSent(fileName: String) {

    }

    override fun saveBuffer(trackBuffer: TrackBuffer) {
        writeLogBufferToFile(trackBuffer)
        trackFiles.update()
    }

//    private fun checkLogFilesSize() {
//        var size = 0L
//        val files = logFiles
//        for (file in files){
//            size += file.length()
//            Log.i("LogFileManager", "name: ${file.name} size: ${file.lastModified()} ")
//        }
//        size/=1024 //kb
//        if (size > prefs.maxLogFilesSize){
//
//        }
//        Log.i("LogFileManager", "checkLogFilesSize: $size")
//
//    }

    override fun saveBuffer(logcatBuffer: LogcatBuffer) {

    }





}