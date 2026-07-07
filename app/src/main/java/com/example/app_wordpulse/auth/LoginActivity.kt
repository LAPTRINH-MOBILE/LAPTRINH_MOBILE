package com.example.app_wordpulse.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_wordpulse.MainActivity
import com.example.app_wordpulse.R
import com.example.app_wordpulse.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = AuthRepository(this)

        // Auto-login check
        repository.getCurrentUser()?.let {
            navigateToMain()
        }

        setupPasswordVisibilityToggle()
        setupListeners()
    }

    private fun setupPasswordVisibilityToggle() {
        setupPasswordToggle(binding.tilPassword, binding.etPassword)
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

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            performLogin()
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.tvError.visibility = View.GONE

        lifecycleScope.launch {
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> {
                    navigateToMain()
                }
                is AuthResult.Error -> {
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
