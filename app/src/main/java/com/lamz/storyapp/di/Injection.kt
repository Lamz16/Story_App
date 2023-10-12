package com.lamz.storyapp.di

import android.content.Context
import com.lamz.storyapp.api.ApiConfig
import com.lamz.storyapp.data.UserRepository
import com.lamz.storyapp.data.pref.UserPreference
import com.lamz.storyapp.data.pref.dataStore


object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}