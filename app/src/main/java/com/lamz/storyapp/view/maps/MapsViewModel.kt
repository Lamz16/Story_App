package com.lamz.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.response.GetListResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession() = repository.getSession().asLiveData()

    private val _location = MutableLiveData<ResultState<GetListResponse>>()
    val location: LiveData<ResultState<GetListResponse>> = _location

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            repository.getStoriesWithLocation(token).asFlow().collect{
                _location.value = it
            }
        }

        }
    }
