package com.lamz.storyapp.view.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.data.pref.UserModel
import com.lamz.storyapp.response.GetListResponse
import com.lamz.storyapp.response.UploadRegisterResponse
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(private val repository: UserRepository) : ViewModel() {

    private val _upload = MutableLiveData<ResultState<UploadRegisterResponse>>()
    val upload: LiveData<ResultState<UploadRegisterResponse>> = _upload
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadImage(token: String, file: File, description: String) {
        viewModelScope.launch {
            repository.uploadImage(token, file, description).asFlow().collect {
                _upload.value = it
            }
        }
    }
}