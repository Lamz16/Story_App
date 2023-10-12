package com.lamz.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.lamz.storyapp.api.ApiService
import com.lamz.storyapp.data.pref.UserModel
import com.lamz.storyapp.data.pref.UserPreference
import com.lamz.storyapp.response.DetailResponse
import com.lamz.storyapp.response.GetListResponse
import com.lamz.storyapp.response.LoginResponse
import com.lamz.storyapp.response.UploadRegisterResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    private val _isLoading = MutableLiveData<Boolean>()
    val Loading: LiveData<Boolean> = _isLoading


    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun registerAccount(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadRegisterResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }

    }

    fun loginAccount(name: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.login(name, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }

    }

    fun getStories(token : String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.getStories("Bearer $token")
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, GetListResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }
    }

    fun getDetailStories(token : String, id: String): LiveData<DetailResponse> = liveData {
        _isLoading.value = true
        try {
            val response = apiService.getDetailStories("Bearer $token",id)
            emit(response)
            _isLoading.value = false
        } catch (e: HttpException) {
            e.message
        }
    }

    fun uploadImage(token : String, imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage("Bearer $token",multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadRegisterResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }

    }



    companion object {

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}