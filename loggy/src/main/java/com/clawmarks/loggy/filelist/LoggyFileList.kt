package com.clawmarks.loggy.filelist

import android.util.Log
import com.clawmarks.loggy.report.ReportType
import com.clawmarks.loggy.LoggyComponent
import com.clawmarks.loggy.context.LoggyContext
import java.io.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class LoggyFileList(override val context: LoggyContext, private val reportType: ReportType) : LoggyComponent {

    private val updateEvent = object : Observable() {
        override fun hasChanged(): Boolean = true
    }

    private val isOverflown: Boolean
        get() {
            val value = size > maxSizeBytes
            if (value) Log.e("LoggyFileList", "${dir.canonicalPath} is overflown!")
            return value
        }

    override val componentName: String
        get() = super.componentName + reportType

    init {
        register()
    }

    private var path: String = if (reportType == ReportType.ANALYTIC) prefs.analyticsPath else prefs.logcatPath
    private var dir = File(path)
    private var limitSizeBytes: Int = 1_048_576
    private var maxSizeBytes: Int = 4_194_304

    val list = CopyOnWriteArrayList<File>()
    private val oldList = ArrayList<File>()

    var size: Long = 0
    val sizeInMb: Long
        get() = size / 1024 / 1024

    var isUpdating = false
    fun updateFileList() {
        if (isUpdating) return
        isUpdating = true
            try {
                list.clear()
                dir.listFiles()
                        ?.filter { it.name.endsWith(".log") }
                        ?.sortedBy { it.lastModified() }
                        ?.let { list.addAll(it) }
                if (!oldList.containsAll(list)) updateEvent.notifyObservers()
                oldList.clear()
                oldList.addAll(list)
                measure()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isUpdating = false
            }
    }

    private fun deleteFileFromDir(file: File) {
        file.delete()
        if (!file.exists()) {
            list.remove(file)
        } else {
            throw Exception("File ${file.name} was not deleted from directory $path")
        }
    }

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
        maxSizeBytes = prefs.maxDirSizeBytes
        path = if (reportType == ReportType.ANALYTIC) prefs.analyticsPath else prefs.logcatPath
        dir = File(path)
        updateFileList()
    }

    val state = object : LoggyFileListState {
        override fun update() {
            updateFileList()
        }

        override val isNotOverflown: Boolean
            get() = !this@LoggyFileList.isOverflown
    }

    fun subscribeUpdates(observer: Observer) {
        updateEvent.addObserver(observer)
    }

    fun unsubscribeUpdates(observer: Observer) {
        updateEvent.deleteObserver(observer)
    }

}