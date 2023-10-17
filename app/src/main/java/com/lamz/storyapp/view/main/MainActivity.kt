package com.lamz.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lamz.storyapp.adapter.ListStoriesAdapter
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.databinding.ActivityMainBinding
import com.lamz.storyapp.view.ViewModelFactory
import com.lamz.storyapp.view.camera.CameraActivity
import com.lamz.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var _binding: ActivityMainBinding? = null
    private val binding get() =  _binding

    val internet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getSession()

        setupView()
        setupAction()
        startAction()
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }else{

                val layoutManager = LinearLayoutManager(this)
                binding?.rvListStory?.layoutManager = layoutManager
                val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
                binding?.rvListStory?.addItemDecoration(itemDecoration)

                viewModel.getStories(user.token).observe(this){story ->


                    if (story != null) {
                        when (story) {
                            is ResultState.Loading -> {
                                binding?.progressBar?.visibility = View.VISIBLE
                            }
                            is ResultState.Success -> {
                                binding?.progressBar?.visibility = View.GONE
                                val storyAdapter = ListStoriesAdapter()
                                val storyData = story.data.listStory
                                storyAdapter.submitList(storyData)
                                binding?.rvListStory?.adapter = storyAdapter
                            }
                            is ResultState.Error -> {
                                binding?.progressBar?.visibility = View.VISIBLE
                                Toast.makeText(this, "Error"+ story.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        binding?.storyAdd?.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }


    private fun startAction() {
        ObjectAnimator.ofFloat(binding?.tagPage, View.TRANSLATION_Y,-30f,30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding?.tagPage, View.TRANSLATION_X,-30f,30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val name = ObjectAnimator.ofFloat(binding?.tagPage, View.ALPHA, 1f).setDuration(100)
        val btnLogout = ObjectAnimator.ofFloat(binding?.logoutButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(name,btnLogout)
            start()
        }

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding?.logoutButton?.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Yakin ingin keluar?")
                setPositiveButton("Ya") { _, _ ->
                    viewModel.logout()
                }
                setNegativeButton("Tidak"){_,_ -> }
                create()
                show()
            }

        }
    }

}