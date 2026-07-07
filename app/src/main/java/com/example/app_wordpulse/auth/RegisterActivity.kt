package com.example.app_wordpulse.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R
import com.example.app_wordpulse.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = AuthRepository(this)

        setupPasswordVisibilityToggles()
        setupSignInLinkStyle()
        setupListeners()
        setupPasswordStrength()
        setupConfirmPasswordValidation()
    }

    private fun setupPasswordVisibilityToggles() {
        setupPasswordToggle(binding.tilPassword, binding.etPassword)
        setupPasswordToggle(binding.tilConfirmPassword, binding.etConfirmPassword)
    }

    private fun setupPasswordToggle(textInputLayout: TextInputLayout, editText: EditText) {
        var isPasswordVisible = false
        editText.transformationMethod = PasswordTransformationMethod.getInstance()
        textInputLayout.setEndIconDrawable(R.drawable.ic_eye)
        textInputLayout.setEndIconContentDescription("Hiện mật khẩu")

        textInputLayout.setEndIconOnClickListener {
            val selection = editText.selectionEnd
            isPasswordVisible = !isPasswordVisible

            editText.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }

            textInputLayout.setEndIconDrawable(
                if (isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
            )
            textInputLayout.setEndIconContentDescription(
                if (isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
            )

            if (selection >= 0) {
                editText.setSelection(selection.coerceAtMost(editText.text?.length ?: 0))
            }
        }
    }

    private fun setupSignInLinkStyle() {
        val text = "Đã có tài khoản? Đăng nhập"
        val signInStart = text.indexOf("Đăng nhập")
        if (signInStart < 0) return

        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#18B9D2")),
            signInStart,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            signInStart,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvSignInLink.text = spannable
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
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
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
            score <= 25 -> "Yếu"
            score <= 75 -> "Trung bình"
            else -> "Mạnh"
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
            binding.tilConfirmPassword.error = "Mật khẩu xác nhận không khớp."
            return
        }

        if (password.length < 6) {
            binding.tvError.text = "Mật khẩu phải có ít nhất 6 ký tự."
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
