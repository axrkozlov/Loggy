package com.clawmarks.logtracker.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.clawmarks.loggy.uploader.LoggyUploader
import com.google.gson.JsonElement
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse
import retrofit2.http.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import kotlin.coroutines.suspendCoroutine

class LoggyUploaderImpl: LoggyUploader {


    //    private var totalFileLength: Long = 0
    private var totalFileUploaded: Long = 0
    private var filekey = "uploadedFile"
    private val uploadInterface: UploadInterface
    private var auth_token = ""
    private lateinit var responses: Array<String?>

    private val coroutineScope = CoroutineScope(Dispatchers.Unconfined + Job())


    private interface UploadInterface {
        @Multipart
        @POST("v01/Logs/UploadOrgFile")
        fun uploadFile(@Part file: MultipartBody.Part?): Call<Void?>
    }

    inner class PRRequestBody(private val mFile: File?) : RequestBody() {
        override fun contentType(): MediaType? {
            // i want to upload only images
            return MediaType.parse("text/plain")
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return mFile!!.length()
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val fileLength = mFile!!.length()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val `in` = FileInputStream(mFile)
            var uploaded: Long = 0
            try {
                var read: Int
                val handler = Handler(Looper.getMainLooper())
                while (`in`.read(buffer).also { read = it } != -1) {

                    // update progress on UI thread
//                    handler.post(ProgressUpdater(uploaded, fileLength))
                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                }
            } finally {
                `in`.close()
            }
        }


    }


    var call: Call<JsonElement>? = null
    override fun uploadSingleFile(file: File) :Boolean {
        var result = false
            val fileBody = PRRequestBody(file)
            val filePart = MultipartBody.Part.createFormData(filekey, file.name, fileBody)
            try {
                val response =
                    safeApiCall (uploadInterface.uploadFile(filePart))



                Log.i("LogUploader", "uploadSingleFile: ${response}")
                result = response is Result.Success
            } catch (e: Exception) {
                e.printStackTrace()
            }

       return result
    }

    override fun cancel() {
        currentCall?.cancel()
    }

    private var currentCall:Call<*>?=null


    init {
        uploadInterface = ApiClient.client.create(UploadInterface::class.java)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

     fun <T : Any> safeApiCall(call: Call<T?>): Result<T?> {
        try {
            currentCall = call
            val response = call.execute()
            if (response.isSuccessful)
                return Result.Success(response.body())
            return Result.Error(IOException("Error Occurred during getting safe Api result"))
        } catch (e: Exception) {
            return Result.Error(e)
        } finally {
            currentCall=null
        }
    }

    sealed  class Result<out T: Any?> {
        data class Success<out T : Any?>(val data: T?) : Result<T?>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }

}