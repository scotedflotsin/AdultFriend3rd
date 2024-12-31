package com.fetlife.imetlife

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fetlife.imetlife.Firebase.CreateUser
import com.fetlife.imetlife.databinding.ActivitySetupProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker

class SetupProfile : AppCompatActivity() {
     lateinit var binding: ActivitySetupProfileBinding
     var email:String = "";
    var pass:String = "";
    var imgUri:String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //get Intent email and pass
        var intent = intent;
        email = intent.getStringExtra("email").toString();
        pass = intent.getStringExtra("pass").toString();


        binding.addProfile.setOnClickListener{
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
        binding.backbutton.setOnClickListener {
            finish()
        }

        binding.gender.setOnClickListener {
            // Options to display
            val options = arrayOf("Male", "Female")

            // Create the AlertDialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select an Option")

            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> binding.gender.setText("Male")
                    1 -> binding.gender.setText("Female")
                }
                dialog.dismiss() // Close the dialog
            }

            // Show the dialog
            builder.create().show()
        }
        binding.find.setOnClickListener {
            // Options to display
            val options = arrayOf("Guy", "Girl")

            // Create the AlertDialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select an Option")

            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> binding.find.setText("Guy")
                    1 -> binding.find.setText("Girl")
                }
                dialog.dismiss() // Close the dialog
            }

            // Show the dialog
            builder.create().show()
        }
        binding.createaccount.setOnClickListener{
            binding.progressBar.visibility = View.VISIBLE
            binding.name.clearFocus();
            binding.name.isEnabled = false;
            binding.gender.clearFocus();
            binding.gender.isEnabled = false;
            binding.find.clearFocus();
            binding.find.isEnabled = false;
            binding.profileage.clearFocus();
            binding.profileage.isEnabled = false;
            binding.about.clearFocus();
            binding.about.isEnabled = false
           if(imgUri.isNotEmpty()&&binding.name.text.toString().isNotEmpty() &&binding.gender.text.toString().isNotEmpty()&& binding.find.text.toString().isNotEmpty()&& binding.profileage.text.toString().isNotEmpty()&& binding.about.text.toString().isNotEmpty()) {
               var emailf = email;
               var pass = pass;
               var username = binding.name.text.toString();
               var gender = binding.gender.text.toString();
               var find = binding.find.text.toString();
               var age = binding.profileage.text.toString();
               var about = binding.about.text.toString();
               var uri = imgUri.toString();
               CreateUser().createAccountAndSaveDetails(
                   this@SetupProfile,
                   emailf,
                   pass,
                   username,
                   gender,
                   find,
                   age,
                   about,
                   Uri.parse(uri)
               ) { success, message ->
                   if (success) {
                       Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                       // Navigate to the next screen
                       binding.progressBar.visibility = View.GONE
                       val intent = Intent(this, MainActivity::class.java)
                       startActivity(intent)
                       this@SetupProfile.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                       finish()

                   } else {
                       Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                       binding.progressBar.visibility = View.GONE
                       binding.name.isEnabled = true;
                       binding.gender.isEnabled = true;
                       binding.find.isEnabled = true;
                       binding.profileage.isEnabled = true;
                       binding.about.isEnabled = true;
                   }
               }
           }else{
               binding.progressBar.visibility = View.GONE
               Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
               binding.name.isEnabled = true;
               binding.gender.isEnabled = true;
               binding.find.isEnabled = true;
               binding.profileage.isEnabled = true;
               binding.about.isEnabled = true;
           }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            // Use Uri object instead of File to avoid storage permissions
            binding.profileImagechat.setImageURI(uri)
            imgUri = uri.toString();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}