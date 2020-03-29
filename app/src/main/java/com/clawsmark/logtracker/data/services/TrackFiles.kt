package com.clawsmark.logtracker.data.services

import java.io.File

class TrackFiles(private val dir: File, private val maxSizeKb: Int) : ArrayList<File>() {

    var sizeInKb: Long = 0

    init {
        update()
    }

    fun update() {
        clear()
        addAll(dir.listFiles()
                .sortedBy { it.lastModified() }
        )
        measure()
    }

    val eldestFile:File?
    get(){
        this.forEach {
            if (it.exists()) return it
        }
        return null
    }

    private fun measure() {
        var sizeInB = 0L
        this.forEach {
            sizeInB += it.length()
        }
        sizeInKb = sizeInB / 1024
        cropToSize()
    }

    private fun cropToSize() {
        if (sizeInKb > maxSizeKb && size > 0) {
            try {
                deleteFileFromDir(this[0])
                measure()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteFileFromDir(file: File) {
        file.delete()
        if (!file.exists()) this.remove(file)
        else throw Exception("File ${file.name} was not deleted from directory $dir")
    }


}