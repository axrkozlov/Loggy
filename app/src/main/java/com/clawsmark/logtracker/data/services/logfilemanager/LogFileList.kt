package com.clawsmark.logtracker.data.services.logfilemanager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

class LogFileList(private val dir: File, private val maxSizeKb: Int) : CopyOnWriteArrayList<File>() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    var sizeInKb: Long = 0

    init {
        try {
            if (!dir.exists()) dir.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        update()
    }

    fun update() {
        coroutineScope.launch {
            clear()
            addAll(dir.listFiles()
                    .filter { it.name.contains("log") }
                    .sortedBy { it.lastModified() }
            )
            measure()
        }
    }


    suspend fun deleteFileFromDir(file: File) {
        coroutineScope.launch {
            file.delete()
            if (!file.exists()) this@LogFileList.remove(file)
            else throw Exception("File ${file.name} was not deleted from directory $dir")
        }
    }

    fun deleteFileFromDir(fileName: String) {
        coroutineScope.launch {
            this@LogFileList.forEach {
                if (it.name == fileName) deleteFileFromDir(it)
            }
        }
    }

    private fun cropToSize() {
        coroutineScope.launch {
            if (sizeInKb > maxSizeKb && size > 0) {
                try {
                    deleteFileFromDir(this@LogFileList[0])
                    measure()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun measure() {
        var sizeInB = 0L
        this.forEach {
            sizeInB += it.length()
        }
        sizeInKb = sizeInB / 1024
        cropToSize()
    }

}