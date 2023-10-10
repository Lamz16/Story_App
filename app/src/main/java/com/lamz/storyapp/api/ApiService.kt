package com.lamz.storyapp.api

import com.lamz.storyapp.response.DetailResponse
import com.lamz.storyapp.response.GetListResponse
import com.lamz.storyapp.response.LoginResponse
import com.lamz.storyapp.response.RegisterResponse
import com.lamz.storyapp.response.Story
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse


    @GET("stories")
    suspend fun getStories(): GetListResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(@Path("id") id : String) : DetailResponse
}