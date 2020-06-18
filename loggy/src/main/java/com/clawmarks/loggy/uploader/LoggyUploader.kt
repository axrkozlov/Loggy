package com.clawmarks.loggy.uploader

import com.clawmarks.loggy.LoggyComponent
import java.io.File

/**
 * Interface LoggyUploader must be implemented as synchronous function, file must be sent one by one
 */
interface LoggyUploader : LoggyComponent {

    /**
     * Mime zlib file type for an api. Use it if loggy compression enabled
     */
    val mimeZlibFileType
        get() = "application/zlib"

    /**
     * Mime text file type for an api. Use it if loggy compression disabled
     */
    val mimeTextFileType
        get() = "text/plain"

    /**
     * Uploading files one by one, sending precess uses coroutine Dispatchers.IO synchronously
     * therefore method must be implemented without background workers
     */
    fun uploadSingleFile(file: File): UploadResult

    /**
     * Uploading files can be cancelled when needed
     */
    fun cancel()
}