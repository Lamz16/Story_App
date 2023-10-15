package com.lamz.storyapp.view.login

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
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.databinding.ActivityLoginBinding
import com.lamz.storyapp.view.ViewModelFactory
import com.lamz.storyapp.view.main.MainActivity
import com.lamz.storyapp.view.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        setupView()
        setupAction()
        startAnimation()
    }

    private fun startAnimation() {
        ObjectAnimator.ofFloat(binding?.imageView, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailLay =
            ObjectAnimator.ofFloat(binding?.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordLay =
            ObjectAnimator.ofFloat(binding?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding?.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, message, emailLay, passwordLay, btnLogin)
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
        binding?.loginButton?.setOnClickListener {
            val email = binding?.emailEditText?.text.toString()
            val password = binding?.passwordEditText?.text.toString()

            viewModel.getData(email, password).observe(this) { user ->
                if (user != null) {
                    when (user) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            user.data.message?.let { it1 -> showToast(it1) }
                            AlertDialog.Builder(this).apply {
                                setTitle("Yeah!")
                                setMessage("Anda berhasil login. Sudah tidak sabar untuk Menulis ya?")
                                setPositiveButton("Lanjut") { _, _ ->
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }

                            showLoading(false)
                        }

                        is ResultState.Error -> {
                            showToast(user.error)
                            AlertDialog.Builder(this).apply {
                                setTitle("Oops !!!")
                                setMessage("Daftar dulu ya supaya bisa login")
                                setPositiveButton("Daftar") { _, _ ->
                                    val intent = Intent(context, WelcomeActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                setNegativeButton("Login") { _, _ -> }
                                create()
                                show()
                            }
                            showLoading(false)
                        }
                    }
                }
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}