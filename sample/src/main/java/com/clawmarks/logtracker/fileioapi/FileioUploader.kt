package com.clawmarks.logtracker.fileioapi

import android.os.Environment
import android.util.Log
import com.clawmarks.loggy.uploader.LoggyUploader
import com.clawmarks.logtracker.api.LoggyUploaderImpl
import com.google.gson.JsonElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.CRC32
import java.util.zip.Deflater

class FileioUploader : LoggyUploader {
    private lateinit var files: Array<File?>
    var uploadIndex = -1
    private var uploadURL = "/"

    //    private var totalFileLength: Long = 0
    private var totalFileUploaded: Long = 0
    private var filekey = "file"
    private val uploadInterface: UploadInterface
    private var auth_token = ""
    private lateinit var responses: Array<String?>


    private var currentCall: Call<*>? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())


    private interface UploadInterface {
        @Multipart
        @POST("/")
        fun uploadFile(@Part file: MultipartBody.Part?, @Header("Authorization") authorization: String?): Call<JsonElement?>

        @Multipart
        @POST("/")
        fun uploadFile(@Part file: MultipartBody.Part?): Call<JsonElement?>
    }

    inner class PRRequestBody(private val file: File?) : RequestBody() {

        private var compressed: ByteArray
        private var size: Long = 0

        init {
            compressed = compress(file)
        }

        override fun contentType(): MediaType? {
            // i want to upload only images
            return MediaType.parse("application/zlib")
        }

        @Throws(IOException::class)
        override fun contentLength(): Long {
            return size
        }
//
//        @Throws(IOException::class)
//        override fun writeTo(sink: BufferedSink) {
//            val fileLength = mFile!!.length()
//            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//            val `in` = FileInputStream(mFile)
//            var uploaded: Long = 0
//            try {
//                var read: Int
//                val handler = Handler(Looper.getMainLooper())
//                while (`in`.read(buffer).also { read = it } != -1) {
//
//                    // update progress on UI thread
////                    handler.post(ProgressUpdater(uploaded, fileLength))
//                    uploaded += read.toLong()
//                    sink.write(buffer, 0, read)
//                }
//            } finally {
//                `in`.close()
//            }
//        }


        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            sink.write(compressed)
        }

        private fun compress(file: File?): ByteArray {

            if (file == null) return ByteArray(0)
            val deflater = Deflater()
            deflater.setInput(file.readBytes())
            deflater.finish()
            val baos = ByteArrayOutputStream()
            val sdfile = File("${Environment.getExternalStorageDirectory().absolutePath}/loggy", "compressed.zlib")
            val fOut = FileOutputStream(sdfile)
            val buf = ByteArray(8192)
            var byteCount = 0
            while (!deflater.finished()) {
                byteCount = deflater.deflate(buf)
                size +=byteCount
                baos.write(buf, 0, byteCount)
                fOut.write(buf, 0, byteCount)
            }
            deflater.end()

            Log.i("FileioUploader", "counting size = $size")
            //decompress command
            //printf "\x1f\x8b\x08\x00\x00\x00\x00\x00" |cat - ./Downloads/compressed.zlib |gzip -dc > ./Downloads/unzlib.log
            size = baos.size().toLong()
            val crc32 = CRC32()
             val ba = baos.toByteArray()
            crc32.update(ba)

            Log.i("FileioUploader", "baos size = $size")
            Log.i("FileioUploader", "compressed.zlib size = ${sdfile.length()}")
            return ba
        }
    }


    var call: Call<JsonElement>? = null
    override fun uploadSingleFile(file: File): Boolean {
        val fileBody = PRRequestBody(file)
        val filePart = MultipartBody.Part.createFormData(filekey, file.name, fileBody)

        Log.i("LogUploader", "uploadSingleFile: $uploadURL, $auth_token")
        try {
            val response = if (auth_token.isEmpty()) {
                safeApiCall(
                        uploadInterface.uploadFile(filePart)
                )
            } else {
                safeApiCall(
                        uploadInterface.uploadFile(filePart, auth_token)
                )
            }

//            val response = call!!.execute()
            Log.i("LogUploader", "uploadSingleFile: $response")

            return response is Result.Success<*>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    override fun cancel() {
        currentCall?.cancel()
    }


    init {
        uploadInterface = FileioClient.client.create(UploadInterface::class.java)
    }

    fun <T : Any> safeApiCall(call: Call<T?>): LoggyUploaderImpl.Result<T?> {
        try {
            currentCall = call
            val response = call.execute()
            if (response.isSuccessful)
                return LoggyUploaderImpl.Result.Success(response.body())
            return LoggyUploaderImpl.Result.Error(IOException("Error Occurred during getting safe Api result"))
        } catch (e: Exception) {
            Log.i("LoggyUploaderImpl", "safeApiCall: $e")
            return LoggyUploaderImpl.Result.Error(e)
        } finally {
            currentCall = null
        }
    }

    data class FileIoResponse(
            val success: Boolean,
            val key: String,
            val link: String,
            val expiry: String
    )

    sealed class Result<out T : Any?> {
        data class Success<out T : Any?>(val data: T?) : Result<T?>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }
}
