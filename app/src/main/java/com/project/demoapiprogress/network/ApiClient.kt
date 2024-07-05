package com.project.demoapiprogress.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    //    const val MEDIA_BASE_URL = "http://thurbal.drudotstech.com"
    const val BASE_URL = "https://app.thrubal.com"
    private const val API_BASE_URL = "$BASE_URL/api/"
    var instance: ApiInterface

    init {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(AuthInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        instance = retrofit.create(ApiInterface::class.java)
    }

    /**
     * Interceptor to add auth token to requests
     */
    private class AuthInterceptor() : Interceptor {
        var token: String = "230|v3kQfiBxePtuvS2T18aQX8kviXYX25QtrqCIvrTV33f228cc"

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()
            builder.addHeader("Authorization", "Bearer $token")
            return chain.proceed(builder.build())
        }
    }

}