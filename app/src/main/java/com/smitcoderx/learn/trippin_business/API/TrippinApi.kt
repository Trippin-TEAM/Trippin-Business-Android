package com.smitcoderx.learn.trippin_business.API

import com.smitcoderx.learn.trippin_business.Model.Login
import com.smitcoderx.learn.trippin_business.Model.Register
import com.smitcoderx.learn.trippin_business.Model.Review.Reviews
import com.smitcoderx.learn.trippin_business.Model.UploadImage
import com.smitcoderx.learn.trippin_business.Model.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface TrippinApi {

    @POST("business_register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("mobile_no") mobileNo: String,
        @Query("address") address: String,
        @Query("desc") desc: String,
        @Query("city") city: String,
        @Query("type") type: String,
    ): Response<Register>

    @POST("login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<Login>

    @GET("me")
    suspend fun getMe(
        @Header("x-access-token") token: String
    ): Response<User>

    @GET("/get_reviews/{id}")
    suspend fun getReviews(
        @Path("id") id: String
    ): Response<Reviews>

    @Multipart
    @POST("/upload_images")
    suspend fun sendImage(
        @Header("x-access-token") token: String,
        @Part file: MultipartBody.Part
    ): Response<UploadImage>
}