package com.fetclubbella.fetclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fetclubbella.fetclub.Firebase.FetchUserData
import com.fetclubbella.fetclub.Utils.CToast
import com.fetclubbella.fetclub.Utils.CustomIntent
import com.fetclubbella.fetclub.Utils.SaveUserDetails
import com.fetclubbella.fetclub.databinding.ActivityAccountCreationBinding
import com.google.firebase.auth.FirebaseAuth


class AccountCreation : AppCompatActivity() {
    var authMethod: Boolean = true;
    private lateinit var binding: ActivityAccountCreationBinding;
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Get the intent
        val intent = intent
        val mode = intent.getIntExtra("mode", 0)
        if (mode == 1) {
            signUpUIChange()
            authMethod = true
        } else {
            signInUIChange()
            authMethod = false

        }
        binding.signbutton.setOnClickListener {
            authMethod = true
            signUpUIChange()
        }
        binding.loginbutton.setOnClickListener {
            authMethod = false
            signInUIChange()
        }
        binding.linearLayout10.setOnClickListener {
            authMethod = !authMethod // Toggle the value
            if (authMethod) {
                signUpUIChange()
            } else {
                signInUIChange()
            }
        }
        binding.backbutton.setOnClickListener {
            finish();
            this@AccountCreation.overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
        binding.createauth.setOnClickListener {
            if (authMethod) {
                val email = binding.editTextTextEmailAddress.text.toString();
                val pass = binding.editTextTextPassword.text.toString();
                if (email != "" && pass != "") {
                    if (pass.length >= 8) {
                        val intent = Intent(this@AccountCreation, SetupProfile::class.java);
                        intent.putExtra("email", email)
                        intent.putExtra("pass", pass)
                        startActivity(intent);
                        this@AccountCreation.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    } else {
                        CToast().showToast(this@AccountCreation, "Password at least 8 characters!");
                    }

                } else {
                    CToast().showToast(this@AccountCreation, "Please enter email and password");
                }
            } else {
                val email = binding.editTextTextEmailAddress.text.toString();
                val pass = binding.editTextTextPassword.text.toString();
                if (email != "" && pass != "") {
                    binding.progressBar.visibility = View.VISIBLE
                    login(email, pass);
                } else {
                    CToast().showToast(this@AccountCreation, "Please enter email and password");
                }
            }

        }
    }

    private fun signInUIChange() {
        runOnUiThread {
            binding.createauth.setText("Log In");
            binding.logimg.visibility = View.VISIBLE;
            binding.signimg.visibility = View.INVISIBLE;
            binding.signintext.setTextColor(getResources().getColor(R.color.white));
            binding.signuptext.setTextColor(getResources().getColor(R.color.maroon));
            binding.haveacccount.setText("I don't have an account?")
            binding.op.setText("Sign Up")

        }
    }

    fun signUpUIChange() {
        runOnUiThread {
            binding.createauth.setText("Sign Up");
            binding.logimg.visibility = View.INVISIBLE;
            binding.signimg.visibility = View.VISIBLE;
            binding.signintext.setTextColor(getResources().getColor(R.color.maroon));
            binding.signuptext.setTextColor(getResources().getColor(R.color.white));
            binding.haveacccount.setText("I have and account?")
            binding.op.setText("Sign In")
        }
    }

    private fun login(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE  // Show the progress bar when login starts

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        // Fetch user data by UID
                        FetchUserData().fetchUserDataByUid(
                            uid,
                            onSuccess = { userData ->
                                // Process the fetched user data
                                val username = userData["username"] as? String
                                val emails = userData["email"] as? String
                                val age = userData["age"] as? String
                                val gender = userData["gender"] as? String
                                val find = userData["find"] as? String
                                val imageUrl = userData["imageUrl"] as? String
                                val about = userData["about"] as? String
                                val password = userData["password"] as? String

                                // Ensure all user data is available
                                if (username != null && age != null && emails!=null && gender != null && find != null && imageUrl != null && password != null && about != null) {

                                    // Save the user details locally
                                    SaveUserDetails().saveUserDetailsLocally(
                                        this@AccountCreation,
                                        email,
                                        password,
                                        username,
                                        gender,
                                        find,
                                        age,
                                        about,
                                        imageUrl,
                                       "true"
                                    )

                                    // Proceed to the next activity
                                    CustomIntent().createIntent(this@AccountCreation, MainActivity::class.java.name)
                                    this@AccountCreation.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                    finish()
                                } else {
                                    // Handle missing user data
                                    binding.progressBar.visibility = View.GONE
                                    CToast().showToast(this@AccountCreation, "Incomplete user data")
                                }
                            },
                            onFailure = { exception ->
                                // Handle failure to fetch user data
                                binding.progressBar.visibility = View.GONE
                                CToast().showToast(this@AccountCreation, "Failed to fetch user data: ${exception.message}")
                                println("Failed to fetch user data: ${exception.message}")
                            }
                        )
                    } else {
                        binding.progressBar.visibility = View.GONE
                        CToast().showToast(this@AccountCreation, "No authenticated user found")
                    }

                } else {
                    // Handle sign-in failure
                    binding.progressBar.visibility = View.GONE
                    CToast().showToast(this@AccountCreation, "ERROR! ${task.exception?.message}")
                    println("Sign-in failed: ${task.exception?.message}")
                }
            }
    }
}