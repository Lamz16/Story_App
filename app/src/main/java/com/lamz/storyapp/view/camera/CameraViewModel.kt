package com.lamz.storyapp.view.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.data.pref.UserModel
import java.io.File

class CameraViewModel(private val repository: UserRepository) : ViewModel(){
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)
}