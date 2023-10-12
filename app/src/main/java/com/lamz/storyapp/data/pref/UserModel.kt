package com.lamz.storyapp.data.pref

data class UserModel(
    val email: String,
    var token: String,
    val isLogin: Boolean = false
)