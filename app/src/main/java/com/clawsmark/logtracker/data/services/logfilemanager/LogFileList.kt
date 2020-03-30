package com.clawsmark.logtracker.data.services.logfilemanager

import kotlinx.coroutines.*
import java.io.*
import java.util.concurrent.CopyOnWriteArrayList

class LogFileList(private val dir: File, private val maxSizeKb: Int) : ArrayList<File>() {

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


    suspend fun deleteFileFromDir(file: File) = coroutineScope {
        file.delete()
        if (!file.exists()) this@LogFileList.remove(file)
        else throw Exception("File ${file.name} was not deleted from directory $dir")

    }

    suspend fun deleteFileFromDir(fileName: String) = coroutineScope {
        this@LogFileList.forEach {
            if (it.name == fileName) deleteFileFromDir(it)
        }
    }

    private suspend fun cropToSize() {
        coroutineScope {
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

    private suspend fun measure() {
        var sizeInB = 0L
        this@LogFileList.forEach {
            sizeInB += it.length()
        }
        sizeInKb = sizeInB / 1024
        cropToSize()
    }

}