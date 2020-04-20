package com.clawsmark.logtracker.data.services.logfilemanager

import com.clawsmark.logtracker.data.ReportType
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import kotlinx.coroutines.*
import java.io.*

class LoggyFileList(override val context: LoggyContext, private val reportType: ReportType) : LoggyComponent {
    init {
        register()
    }
    private val path: String = if (reportType == ReportType.ANALYTIC) prefs.analyticsPath else prefs.logcatPath
    private val dir = File(path)
    private var limitSizeBytes: Int = 1_048_576
    val list = ArrayList<File>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    var size: Long = 0
    var sizeInMb: Long = 0
        get() = size / 1024 / 1024

    var isUpdating = false
    fun update() {
        if (isUpdating) return
        isUpdating = true
        coroutineScope.launch {
            try {
                list.clear()
                list.addAll(dir.listFiles()
                        .filter { it.name.endsWith(".log") }
                        .sortedBy { it.lastModified() }
                )
                measure()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isUpdating = false
            }

        }
    }

//    var lockedFile: File? = null
//    var fileMustBeDeleted : File? = null
//    fun lockFileWhileReading(file: File) {
//        lockedFile = file
//    }
//
//    fun unLockFile() {
//        lockedFile = null
//    }

    private fun deleteFileFromDir(file: File) {
        file.delete()
        if (!file.exists()) {
            list.remove(file)
        } else {
            throw Exception("File ${file.name} was not deleted from directory $path")
        }
    }
//
//    private fun deleteFileFromDir(fileName: String) {
//        list.forEach {
//            if (it.name == fileName) deleteFileFromDir(it)
//        }
//    }

    private fun cropToSize() {
        if (size > limitSizeBytes && list.size > 0) {
            deleteFileFromDir(list[0])
            measure()
        }
    }

    private fun measure() {
        size = 0L
        list.forEach {
            size += it.length()
        }
        cropToSize()
    }

    override fun onPrefsUpdated() {
        limitSizeBytes = prefs.dirSizeBytes
        update()
    }

}