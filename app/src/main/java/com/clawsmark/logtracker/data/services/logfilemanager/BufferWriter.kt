package com.clawsmark.logtracker.data.services.logfilemanager

import com.clawsmark.logtracker.data.Buffer
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class BufferWriter(private val dir: File, override val successCallback: () -> Unit) : BufferWritable {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    private val format = SimpleDateFormat("ddMMyy_HHmmss", Locale.getDefault())

    override fun write(buffer: Buffer<*>) {
        coroutineScope.launch {
            try {
                val humanDate = format.format(Date(System.currentTimeMillis()))
                val logFile = File(dir, "$humanDate.log")

                val fOut = FileOutputStream(logFile)
                val osw = OutputStreamWriter(fOut)
                for (item in buffer) {
                    osw.write("$item\n")
                }
                osw.flush()
                osw.close()
                notifySuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun notifySuccess() {
        successCallback.invoke()
    }

}