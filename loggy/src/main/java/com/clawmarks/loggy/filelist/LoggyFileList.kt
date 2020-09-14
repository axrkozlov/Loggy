package com.clawmarks.loggy.filelist

import android.util.Log
import com.clawmarks.loggy.report.ReportType
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext
import java.io.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

abstract class LoggyFileList(override val context: LoggyContext) : LoggyContextComponent {

    abstract val reportType: ReportType
    abstract val path: String
    private val dir: File by lazy { File(path) }

    private val updateEvent = object : Observable() {
        override fun hasChanged(): Boolean = true
    }

    private val isOverflown: Boolean
        get() {
            val value = size > maxSizeBytes
            if (value) Log.e(componentName, "${dir.canonicalPath} is overflown!")
            return value
        }

    private var limitSizeBytes: Int = 1_048_576
    private var maxSizeBytes: Int = 4_194_304

    val list = CopyOnWriteArrayList<File>()
    private val oldList = ArrayList<File>()

    var size: Long = 0

    var isUpdating = false
    fun updateFileList() {
        if (isUpdating) return
        isUpdating = true
        try {
            list.clear()
            dir.listFiles()
                    ?.filter { it.name.endsWith(context.fileExtension) }
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