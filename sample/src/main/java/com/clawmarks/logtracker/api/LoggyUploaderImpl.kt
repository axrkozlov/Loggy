package com.clawmarks.logtracker.api

import android.os.Environment
import android.util.Log
import com.clawmarks.loggy.Loggy
import com.clawmarks.loggy.uploader.LoggyUploader
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.*
import java.util.zip.Deflater


class LoggyUploaderImpl : LoggyUploader {

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

//        @Throws(IOException::class)
//        override fun writeTo(sink: BufferedSink) {
//            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//            val inputStream = FileInputStream(mFile)
//            var uploaded: Long = 0
//            inputStream.use { input ->
//                var read: Int
//                while (input.read(buffer).also { read = it } != -1) {
//                    uploaded += read.toLong()
//                    sink.write(buffer, 0, read)
//                }
//            }
//
//            val compressed = compress(mFile)
//            sink.write(compressed)
//        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val compressed = compress(mFile)
            sink.write(compressed)
        }

        private fun compress(file: File?): ByteArray {
//            val inputString = "blahblahblah"
//            val input = file.toByteArray(charset("UTF-8"))
//            val output = ByteArray()
//            val compresser = Deflater()
//            compresser.setInput(file)
//            compresser.finish()
//            val compressedDataLength: Int = compresser.deflate(output)
//            compresser.end()
            if (file == null) return ByteArray(0)
            val deflater = Deflater()
            deflater.setInput(file.readBytes())
            deflater.finish()

            val baos = ByteArrayOutputStream()
            val fOut = FileOutputStream(File("${Environment.getExternalStorageDirectory().absolutePath}/loggy", "compressed.zlib"))
            val buf = ByteArray(8192)
            while (!deflater.finished()) {
                val byteCount = deflater.deflate(buf)
                baos.write(buf, 0, byteCount)
                fOut.write(buf, 0, byteCount)
            }
            deflater.end()


            return baos.toByteArray()
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