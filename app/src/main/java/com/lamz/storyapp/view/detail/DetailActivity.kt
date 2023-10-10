package com.lamz.storyapp.view.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.lamz.storyapp.databinding.ActivityDetailBinding
import com.lamz.storyapp.response.DetailResponse
import com.lamz.storyapp.view.ViewModelFactory
import com.lamz.storyapp.view.welcome.WelcomeActivity

class DetailActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        val detail = intent.getStringExtra(EXTRA_ID)
        if (detail != null) {
            viewModel.getDetailStories(detail).observe(this) { story ->
                setDetailStory(story)
            }
        }


    }

    private fun setDetailStory(story: DetailResponse) {
        binding?.tvAuth?.text = story.story?.name
        binding?.tvDesc?.text = story.story?.description
        Glide.with(this).load(story.story?.photoUrl)
            .centerCrop()
            .into(binding?.imgDetail!!)
    }

    companion object {

        const val EXTRA_ID = "extra_id"
    }
}