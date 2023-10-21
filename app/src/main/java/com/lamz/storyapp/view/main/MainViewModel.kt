package com.lamz.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.response.GetListResponse
import com.lamz.storyapp.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession() = repository.getSession().asLiveData()
    private val _story = MutableLiveData<ResultState<GetListResponse>>()
    val story: LiveData<ResultState<GetListResponse>> = _story

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> = repository.getStories(token).cachedIn(viewModelScope)

}