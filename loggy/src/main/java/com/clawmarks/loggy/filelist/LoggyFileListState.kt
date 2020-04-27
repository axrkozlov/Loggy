package com.clawmarks.loggy.filelist

interface LoggyFileListState {
    fun update()
    val isNotOverflown:Boolean
}