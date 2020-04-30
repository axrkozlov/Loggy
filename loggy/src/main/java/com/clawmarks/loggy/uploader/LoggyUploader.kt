package com.clawmarks.loggy.uploader

import org.koin.core.KoinComponent
import java.io.File

/** interface LoggyUploader must be implemented as synchronous function, file must be sent one by one
*/
interface LoggyUploader:KoinComponent {
    fun uploadSingleFile(file: File):Boolean
    fun cancel()
}