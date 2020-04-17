package com.clawsmark.logtracker.data.services.logfilemanager

import android.os.Environment
import android.os.StatFs
import kotlinx.coroutines.*
import java.io.*

class LogFileDir(private val dir: File, private val maxSizeKb: Int) {
    val list =  ArrayList<File>()
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
            list.clear()
            list.addAll(dir.listFiles()
                    .filter { it.name.contains("log") }
                    .sortedBy { it.lastModified() }
            )
            measure()
        }
    }


    suspend fun deleteFileFromDir(file: File) = coroutineScope {
        file.delete()
        if (!file.exists()) list.remove(file)
        else throw Exception("File ${file.name} was not deleted from directory $dir")

    }

    suspend fun deleteFileFromDir(fileName: String) = coroutineScope {
        list.forEach {
            if (it.name == fileName) deleteFileFromDir(it)
        }
    }

    private suspend fun cropToSize() {
        coroutineScope {
            if (sizeInKb > maxSizeKb && list.size > 0) {
                try {
                    deleteFileFromDir(list[0])
                    measure()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun measure() {
        var sizeInB = 0L
        list.forEach {
            sizeInB += it.length()
        }
        sizeInKb = sizeInB / 1024
        cropToSize()

    }

}