package com.clawmarks.logtracker.apiafterstopsending

import android.util.Log
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*
import java.io.IOException

class AnotherServiceImpl {

    private val api: UploadInterface

    private var currentCall: Call<*>? = null

    init {
        api = AnotherApiClient.client.create(UploadInterface::class.java)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    private interface UploadInterface {
        @GET("posts/1")
        fun getPosts(): Call<JsonObject?>
    }

    fun checkPosts(): Boolean {
        val response = safeApiCall(api.getPosts())
        Log.i("LogUploader", "uploadSingleFile: $response")
        return response is Result.Success
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