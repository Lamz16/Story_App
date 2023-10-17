package com.lamz.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.data.pref.UserModel
import com.lamz.storyapp.response.DetailResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _detailStory = MutableLiveData<ResultState<DetailResponse>>()
    val detailStory: LiveData<ResultState<DetailResponse>> = _detailStory

    val detailLoading = repository.repoLoading
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetailStories(token : String, id: String) {
        viewModelScope.launch {
            repository.getDetailStories(token , id).asFlow().collect {
                _detailStory.value = it
            }
        }

    }


}