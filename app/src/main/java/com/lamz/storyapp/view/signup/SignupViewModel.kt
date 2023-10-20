package com.lamz.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.lamz.storyapp.data.UserRepository

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadData(name: String , email : String, password : String) = repository.registerAccount(name,email,password)


}