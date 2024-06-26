package com.lamz.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.response.UploadRegisterResponse
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _upload = MutableLiveData<ResultState<UploadRegisterResponse>>()
    val upload: LiveData<ResultState<UploadRegisterResponse>> = _upload

    fun uploadData(name: String, email: String, password: String) {
        viewModelScope.launch {
            repository.registerAccount(name, email, password).asFlow().collect {
                _upload.value = it
            }
        }
    }


}