package com.clawsmark.logtracker.data.sender

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.JsonElement
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class FileUploader {
    var fileUploaderCallback: FileUploaderCallback? = null
    private lateinit var files: Array<File?>
    var uploadIndex = -1
    private var uploadURL = ""
    private var totalFileLength: Long = 0
    private var totalFileUploaded: Long = 0
    private var filekey = ""
    private val uploadInterface: UploadInterface
    private var auth_token = ""
    private lateinit var responses: Array<String?>
    fun cancel() {
        call!!.cancel()
    }

    private interface UploadInterface {
        @Multipart
        @POST
        fun uploadFile(@Url url: String?, @Part file: MultipartBody.Part?, @Header("Authorization") authorization: String?): Call<JsonElement>?

        @Multipart
        @POST
        fun uploadFile(@Url url: String?, @Part file: MultipartBody.Part?): Call<JsonElement>?
    }

    interface FileUploaderCallback {
        fun onError()
        fun onFinish(responses: Array<String?>)
        fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int)
    }

    inner class PRRequestBody(private val mFile: File?) : RequestBody() {
        override fun contentType(): MediaType? {
            // i want to upload only images
            return MediaType.parse("image/*")
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
                    handler.post(ProgressUpdater(uploaded, fileLength))
                    uploaded += read.toLong()
                    sink.write(buffer, 0, read)
                }
            } finally {
                `in`.close()
            }
        }


    }

    @JvmOverloads
    fun uploadFiles(url: String, filekey: String, files: Array<File?>, fileUploaderCallback: FileUploaderCallback?, auth_token: String = "") {
        this.fileUploaderCallback = fileUploaderCallback
        this.files = files
        uploadIndex = -1
        uploadURL = url
        this.filekey = filekey
        this.auth_token = auth_token
        totalFileUploaded = 0
        totalFileLength = 0
        uploadIndex = -1
        responses = arrayOfNulls(files.size)
        for (i in files.indices) {
            totalFileLength = totalFileLength + files[i]!!.length()
        }
        uploadNext()
    }

    private fun uploadNext() {
        if (files.size > 0) {
            if (uploadIndex != -1) totalFileUploaded = totalFileUploaded + files[uploadIndex]!!.length()
            uploadIndex++
            if (uploadIndex < files.size) {
                uploadSingleFile(uploadIndex)
            } else {
                fileUploaderCallback!!.onFinish(responses)
            }
        } else {
            fileUploaderCallback!!.onFinish(responses)
        }
    }

    var call: Call<JsonElement>? = null
    private fun uploadSingleFile(index: Int) {
        val fileBody = PRRequestBody(files[index])
        val filePart = MultipartBody.Part.createFormData(filekey, files[index]!!.name, fileBody)
//        call = if (auth_token.isEmpty()) {
//            uploadInterface.uploadFile(uploadURL, filePart)
//        } else {
//            uploadInterface.uploadFile(uploadURL, filePart, auth_token)
//        }
        call = uploadInterface.uploadFile("api/v01/Logs/UploadOrgFile", filePart)
        call!!.enqueue(object : Callback<JsonElement?> {
            override fun onResponse(call: Call<JsonElement?>, response: Response<JsonElement?>) {
                if (response.isSuccessful) {
                    val jsonElement = response.body()
                    responses[index] = jsonElement.toString()
                } else {
                    responses[index] = ""
                }
                uploadNext()
            }

            override fun onFailure(call: Call<JsonElement?>, t: Throwable) {
                if (call.isCanceled) {
                    Log.e(ContentValues.TAG, "request was cancelled")
                } else {
                    Log.e(ContentValues.TAG, "other larger issue, i.e. no network connection?")
                    fileUploaderCallback!!.onError()
                }
            }
        })
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) : Runnable {
        override fun run() {
            val current_percent = (100 * mUploaded / mTotal).toInt()
            val total_percent = (100 * (totalFileUploaded + mUploaded) / totalFileLength).toInt()
            fileUploaderCallback!!.onProgressUpdate(current_percent, total_percent, uploadIndex + 1)
        }

    }

    init {
        uploadInterface = ApiClient.client.create(UploadInterface::class.java)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}