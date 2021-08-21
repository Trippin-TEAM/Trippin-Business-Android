package com.smitcoderx.learn.trippin_business.Model.User

data class User(
    val _id: String,
    val address: String,
    val city: String,
    val desc: String,
    val email: String,
    val mobile_no: String,
    val name: String,
    val role: String,
    val type: String,
    val username: String,
    val image: String,
    val average_ratings: Float
)