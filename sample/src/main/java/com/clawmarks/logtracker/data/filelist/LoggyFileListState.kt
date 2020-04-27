package com.clawmarks.logtracker.data.filelist

interface LoggyFileListState {
    fun update()
    val isNotOverflown:Boolean
}