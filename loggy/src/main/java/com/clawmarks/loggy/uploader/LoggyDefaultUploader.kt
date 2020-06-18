package com.clawmarks.loggy.uploader

import android.util.Log
import java.io.File

class LoggyDefaultUploader : LoggyUploader {
    override fun uploadSingleFile(file: File): UploadResult {
        Log.e("DefaultUploader", "LoggyUploader interface must be implemented!")
        return UploadResult.UploadApiError
    }

    override fun cancel() {
        Log.e("DefaultUploader", "LoggyUploader interface must be implemented!")
    }
}