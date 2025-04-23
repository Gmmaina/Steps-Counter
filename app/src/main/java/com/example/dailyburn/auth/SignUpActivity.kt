package com.example.dailyburn.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.dailyburn.R
import com.example.dailyburn.activities.MainActivity
import com.example.dailyburn.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Sign Up Button Click
        binding.btnSignUp.setOnClickListener {
            validateAndRegister()
        }

        // Login Text Click
        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateAndRegister() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        when {
            TextUtils.isEmpty(email) -> {
                binding.tilEmail.error = "Email is required"
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Please enter a valid email"
                return
            }

            TextUtils.isEmpty(password) -> {
                binding.tilPassword.error = "Password is required"
                return
            }

            password.length < 6 -> {
                binding.tilPassword.error = "Password must be at least 6 characters"
                return
            }

            password != confirmPassword -> {
                binding.tilConfirmPassword.error = "Passwords do not match"
                return
            }

            else -> {
                // Clear errors
                binding.tilEmail.error = null
                binding.tilPassword.error = null
                binding.tilConfirmPassword.error = null

                registerUser(email, password)
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignUp.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            // Always hide progress bar and re-enable button
            binding.progressBar.visibility = View.GONE
            binding.btnSignUp.isEnabled = true

            if (task.isSuccessful) {
                // Don't use findNavController here - instead start the main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Get the specific error message
                val errorMessage = when (task.exception?.message) {
                    "The email address is already in use by another account." ->
                        "Email already exists. Please use a different email or try signing in."
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                        "Network error. Please check your internet connection."
                    else ->
                        "Registration failed: ${task.exception?.message}"
                }

                // Show the specific error message
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
