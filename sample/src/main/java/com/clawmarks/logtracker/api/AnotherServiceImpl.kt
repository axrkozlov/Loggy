package com.clawmarks.logtracker.api

import android.util.Log
import com.clawmarks.loggy.uploader.LoggyUploader
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.http.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class AnotherServiceImpl : LoggyUploader {

    private var filekey = "uploadedFile"
    private val uploadInterface: UploadInterface

    private var currentCall: Call<*>? = null

    init {
        uploadInterface = LogApiClient.client.create(UploadInterface::class.java)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    private interface UploadInterface {
        @Multipart
        @POST("v01/Logs/UploadOrgFile")
        fun uploadFile(@Part file: MultipartBody.Part?): Call<Void?>
    }

    inner class PRRequestBody(private val mFile: File?) : RequestBody() {
        override fun contentType(): MediaType? {
            return MediaType.parse("text/plain")
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return mFile!!.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val fis = FileInputStream(mFile)
            var uploaded: Long = 0
            fis.use { `in` ->
                var read: Int
                while (`in`.read(buffer).also { read = it } != -1) {
                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                }
            }
        }
    }

    override fun uploadSingleFile(file: File): Boolean {
        val fileBody = PRRequestBody(file)
        val filePart = MultipartBody.Part.createFormData(filekey, file.name, fileBody)
        val response = safeApiCall(uploadInterface.uploadFile(filePart))
        Log.i("LogUploader", "uploadSingleFile: $response")
        return response is Result.Success
    }

    override fun cancel() {
        Log.i("LoggyUploaderImpl", "cancel: $currentCall")
        currentCall?.cancel()
    }

    fun <T : Any> safeApiCall(call: Call<T?>): Result<T?> {
        try {
            currentCall = call
            val response = call.execute()
            if (response.isSuccessful)
                return Result.Success(response.body())
            return Result.Error(IOException("Error Occurred during getting safe Api result"))
        } catch (e: Exception) {
            Log.i("LoggyUploaderImpl", "safeApiCall: $e")
            return Result.Error(e)
        } finally {
            currentCall = null
        }
    }

    sealed class Result<out T : Any?> {
        data class Success<out T : Any?>(val data: T?) : Result<T?>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

}