package com.clawsmark.logtracker.data.services.logfilemanager

interface LoggyFileListState {
    fun update()
    val isNotOverflown:Boolean
}