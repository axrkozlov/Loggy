package com.clawsmark.logtracker.data.services.logsender

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.clawsmark.logtracker.api.ApiClient
import com.google.gson.JsonElement
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class LogUploader {
    private lateinit var files: Array<File?>
    var uploadIndex = -1
    private var uploadURL = "/"
//    private var totalFileLength: Long = 0
    private var totalFileUploaded: Long = 0
    private var filekey = "file"
    private val uploadInterface: UploadInterface
    private var auth_token = ""
    private lateinit var responses: Array<String?>
    fun cancel() {
        call!!.cancel()
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())


    private interface UploadInterface {
        @Multipart
        @POST("/")
        suspend fun uploadFile(@Part file: MultipartBody.Part?, @Header("Authorization") authorization: String?): Response<FileIoResponse>

        @Multipart
        @POST("/")
        suspend fun uploadFile(@Part file: MultipartBody.Part?): Response<FileIoResponse>
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
            val buffer = ByteArray(Companion.DEFAULT_BUFFER_SIZE)
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
    suspend fun uploadSingleFile(file: File, successCallback:(Boolean)->Unit)=coroutineScope{
        val fileBody = PRRequestBody(file)
        val filePart = MultipartBody.Part.createFormData(filekey, file.name, fileBody)

        Log.i("LogUploader", "uploadSingleFile: $uploadURL, $auth_token")
        try {
            val response =if (auth_token.isEmpty()) {
                safeApiCall {
                    uploadInterface.uploadFile(filePart)
                }
            } else {
                safeApiCall {
                    uploadInterface.uploadFile(filePart, auth_token)
                }
            }

//            val response = call!!.execute()
            Log.i("LogUploader", "uploadSingleFile: ${response}")

//            successCallback.invoke(response.isSuccessful)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    init {
        uploadInterface = ApiClient.client.create(UploadInterface::class.java)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        try {
            val response = call()
            if(response.isSuccessful)
                return Result.Success(response.body()!!)
            return Result.Error(IOException("Error Occurred during getting safe Api result"))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

}
data class FileIoResponse(
        val success:Boolean,
        val key:String,
        val link:String,
        val expiry:String
)
sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}