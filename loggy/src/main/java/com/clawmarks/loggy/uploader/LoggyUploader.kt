package com.clawmarks.loggy.uploader

import com.clawmarks.loggy.LoggyComponent
import java.io.File

/** interface LoggyUploader must be implemented as synchronous function, file must be sent one by one
 */
interface LoggyUploader : LoggyComponent {

    /**
     * Mime file type for an api
     */
    val mimeFileType
        get() = "application/zlib"

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