package com.adultfriendbella.adultfre

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adultfriendbella.adultfre.databinding.ActivityDetailProfileViewBinding
import com.squareup.picasso.Picasso
import java.util.UUID

class DetailProfileView : AppCompatActivity() {
    lateinit var binding:ActivityDetailProfileViewBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailProfileViewBinding.inflate(layoutInflater)
        enableEdgeToEdge() // Enables edge-to-edge UI for supported devices
        setContentView(binding.root) // Ensure the layout is set

        // Ensure that 'main' ID is defined correctly in the layout XML
        val mainView = findViewById<ConstraintLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //get data from intent
        var intent = intent
        var img = intent.getStringExtra("profileImage")
        var name = intent.getStringExtra("profileName")
        var age = intent.getStringExtra("profileAge")
        var about = intent.getStringExtra("profileAbout")
        var userId = intent.getStringExtra("profileId")
        updateDataView(img.toString(),name.toString(),age.toString(),about.toString())

        //buttons
        binding.backbutton.setOnClickListener{
            finish()
        }
        binding.backutton.setOnClickListener{
            Toast.makeText(this@DetailProfileView, "Reported", Toast.LENGTH_SHORT).show()
        }
        binding.promess.setOnClickListener{
            val intent = Intent(this@DetailProfileView, Chatting::class.java)
            intent.putExtra("profileId", userId) // Passing the profile id to ChatActivity
            intent.putExtra("profileImage", img) // Passing the profile image URL to ChatActivity
            intent.putExtra("profileName", name) // Passing the profile name to ChatActivity
            this@DetailProfileView.startActivity(intent)
        }

        binding.prolike.setOnClickListener{
            Toast.makeText(this@DetailProfileView, "Liked", Toast.LENGTH_SHORT).show()

        }

        binding.proclose.setOnClickListener{
            finish()
        }




    }
  fun updateDataView(img:String,name:String,age:String,about:String){
      val imageUrlWithCacheBust = "$img?cacheBust=${UUID.randomUUID()}"
      runOnUiThread{
          binding.progressBar.visibility = View.VISIBLE
          binding.nearProfile.setText(name.toString());
          binding.proabout.setText(about.toString());
          binding.profileage.setText(age.toString()+"Years old");
      }
      Picasso.get()
          .load(imageUrlWithCacheBust)
          .into(binding.webview, object : com.squareup.picasso.Callback {
              override fun onSuccess() {
                  binding.progressBar.visibility = View.GONE
              }

              override fun onError(e: Exception?) {
                  binding.progressBar.visibility = View.GONE
                  Log.e("Chatting", "Error loading profile image: ${e?.message}")
              }
          })
  }
}
