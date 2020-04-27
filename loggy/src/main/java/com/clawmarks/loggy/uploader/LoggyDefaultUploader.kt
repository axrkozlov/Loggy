package com.clawmarks.loggy.uploader

import android.util.Log
import java.io.File

class LoggyDefaultUploader: LoggyUploader{
    override suspend fun uploadSingleFile(file: File, successFunction: (Boolean) -> Unit) {
        Log.e("DefaultUploader", "LoggyUploader interface must be implemented!")
    }
}