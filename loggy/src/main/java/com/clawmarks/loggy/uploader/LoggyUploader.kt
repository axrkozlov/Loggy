package com.clawmarks.loggy.uploader

import org.koin.core.KoinComponent
import java.io.File

interface LoggyUploader:KoinComponent {
    suspend fun uploadSingleFile(file: File, successFunction: (Boolean) -> Unit)
}