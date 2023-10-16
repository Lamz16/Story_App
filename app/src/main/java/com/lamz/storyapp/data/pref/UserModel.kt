package com.lamz.storyapp.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val email: String,
    var token: String,
    val isLogin: Boolean = false
) : Parcelable