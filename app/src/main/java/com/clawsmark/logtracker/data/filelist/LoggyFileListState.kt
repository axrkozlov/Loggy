package com.clawsmark.logtracker.data.filelist

interface LoggyFileListState {
    fun update()
    val isNotOverflown:Boolean
}