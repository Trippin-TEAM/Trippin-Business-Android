package com.smitcoderx.learn.trippin_business.Model.Review

data class ReviewsItem(
    val _id: String,
    val business_id: String,
    val name: String,
    val username: String,
    val ratings: Double,
    val review: String,
    val user_id: String
)