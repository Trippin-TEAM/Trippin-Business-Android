package com.smitcoderx.learn.trippin_business.API

import com.smitcoderx.learn.trippin_business.Util.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    val retrofitService by lazy {
        retrofit.create(TrippinApi::class.java)
    }

}