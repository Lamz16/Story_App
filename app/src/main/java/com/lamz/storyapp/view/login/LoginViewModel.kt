package com.lamz.storyapp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun getData(name: String, password: String) = repository.loginAccount(name, password)
}