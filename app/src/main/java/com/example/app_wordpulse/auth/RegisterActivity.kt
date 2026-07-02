package com.example.app_wordpulse.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R
import com.example.app_wordpulse.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = AuthRepository(this)

        setupListeners()
        setupPasswordStrength()
        setupConfirmPasswordValidation()
    }

    private fun setupConfirmPasswordValidation() {
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateConfirmPassword()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateConfirmPassword() {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
        } else {
            binding.tilConfirmPassword.error = null
        }
    }

    private fun setupPasswordStrength() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.isEmpty()) {
                    binding.layoutStrength.visibility = View.GONE
                } else {
                    binding.layoutStrength.visibility = View.VISIBLE
                    updateStrengthIndicator(password)
                }
                validateConfirmPassword()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateStrengthIndicator(password: String) {
        var score = 0
        if (password.length >= 6) score += 25
        if (password.any { it.isDigit() }) score += 25
        if (password.any { it.isLetter() }) score += 25
        if (password.any { !it.isLetterOrDigit() }) score += 25

        val color = when {
            score <= 25 -> R.color.error
            score <= 75 -> R.color.strength_medium
            else -> R.color.strength_strong
        }

        val label = when {
            score <= 25 -> "Weak"
            score <= 75 -> "Medium"
            else -> "Strong"
        }

        val colorValue = ContextCompat.getColor(this, color)
        binding.strengthBar.progress = score
        binding.strengthBar.progressTintList = ColorStateList.valueOf(colorValue)
        binding.tvStrengthLabel.text = label
        binding.tvStrengthLabel.setTextColor(colorValue)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCreateAccount.setOnClickListener {
            performRegister()
        }

        binding.tvSignInLink.setOnClickListener {
            finish()
        }
    }

    private fun performRegister() {
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        binding.tvError.visibility = View.GONE

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match."
            return
        }

        if (password.length < 6) {
            binding.tvError.text = "Password must be at least 6 characters."
            binding.tvError.visibility = View.VISIBLE
            return
        }

        lifecycleScope.launch {
            when (val result = repository.register(username, email, password)) {
                is AuthResult.Success -> {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is AuthResult.Error -> {
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}
