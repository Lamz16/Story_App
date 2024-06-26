package com.lamz.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lamz.storyapp.R
import com.lamz.storyapp.adapter.ListStoriesAdapter
import com.lamz.storyapp.adapter.LoadingStateAdapter
import com.lamz.storyapp.databinding.ActivityMainBinding
import com.lamz.storyapp.view.ViewModelFactory
import com.lamz.storyapp.view.camera.CameraActivity
import com.lamz.storyapp.view.maps.MapsActivity
import com.lamz.storyapp.view.welcome.WelcomeActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rvListStory?.layoutManager = LinearLayoutManager(this)

        setupView()
        startAction()
        getSession()

        binding?.swipe?.setOnRefreshListener {
            getSession()

            Handler().postDelayed({
                binding?.swipe?.isRefreshing = false
            }, 2000)
        }
    }

    private fun getSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {

                binding?.topAppBar?.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu1 -> {
                            val intent = Intent(this, MapsActivity::class.java)
                            startActivity(intent)
                            true
                        }

                        R.id.menu2 -> {
                            AlertDialog.Builder(this).apply {
                                setTitle("Yakin ingin keluar?")
                                setPositiveButton("Ya") { _, _ ->
                                    viewModel.logout()
                                }
                                setNegativeButton("Tidak") { _, _ -> }
                                create()
                                show()
                            }
                            true
                        }

                        else -> false
                    }
                }

                val adapter = ListStoriesAdapter()
                binding?.rvListStory?.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter{
                        adapter.retry()
                    }
                )
                viewModel.getStories(user.token).observe(this) {
                    if (it != null){
                        binding?.tvError?.visibility = View.GONE
                        adapter.submitData(lifecycle, it)
                    }else{
                        binding?.tvError?.visibility = View.VISIBLE
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
        ObjectAnimator.ofFloat(binding?.tagPage, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding?.tagPage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val name = ObjectAnimator.ofFloat(binding?.tagPage, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(name)
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


}