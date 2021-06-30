package com.example.dankmemes

data class Meme(
    val subreddit: String,
    val title: String,
    val url: String,
    val preview: String,
    val author: String,
    val ups: String
) {
}