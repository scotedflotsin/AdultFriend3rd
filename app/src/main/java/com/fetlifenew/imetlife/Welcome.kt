package com.fetlifenew.imetlife

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fetlifenew.imetlife.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

class Welcome : AppCompatActivity() {
    lateinit var auth: FirebaseAuth;
    private lateinit var binding: ActivityAuthBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@Welcome, MainActivity::class.java)
            startActivity(intent)
            // Call the overridePendingTransition immediately after startActivity
            this@Welcome.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        binding.signup.setOnClickListener {
            val intent = Intent(this@Welcome, AccountCreation::class.java)
            intent.putExtra("mode", 1)
            startActivity(intent)
            // Call the overridePendingTransition immediately after startActivity
            this@Welcome.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.login.setOnClickListener {
            val intent = Intent(this@Welcome, AccountCreation::class.java)
            intent.putExtra("mode", 2)
            startActivity(intent)
            this@Welcome.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }
}