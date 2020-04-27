package com.clawmarks.logtracker.data.sender

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Sheetal on 5/16/18.
 */
object ApiClient {
    //    const val BASE_URL = "https://file.io"
    const val BASE_URL = "http://kon-app2-wt3.hq.bc:8383/api/"
    private var retrofit: Retrofit? = null
    val client: Retrofit
        get() {
            if (retrofit == null) {
                val client = OkHttpClient().newBuilder()
                        .addInterceptor(HttpLoggingInterceptor())
                        .connectTimeout(TIME_OUT_SHORT.toLong(), TimeUnit.SECONDS)
                        .readTimeout(TIME_OUT_SHORT.toLong(), TimeUnit.SECONDS)
                        .writeTimeout(TIME_OUT_SHORT.toLong(), TimeUnit.SECONDS)
                        .build()

                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build()
            }
            return retrofit!!
        }


        private const val TIME_OUT_SHORT = 60
        private const val TIME_OUT_MEDIUM = 120
        private const val TIME_OUT_LONG = 360


}