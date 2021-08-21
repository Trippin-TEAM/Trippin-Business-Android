package com.smitcoderx.learn.trippin_business.Model.Review

data class ReviewsItem(
    val _id: String,
    val business_id: String,
    val business_img: String,
    val business_name: String,
    val business_username: String,
    val name: String,
    val ratings: Double,
    val review: String,
    val user_id: String,
    val username: String
)