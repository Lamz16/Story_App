package com.lamz.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.data.pref.UserModel

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetailStories(id: String) = repository.getDetailStories(id)

}