package com.fetlife.imetlife

import HomeFragment
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.fetlife.imetlife.Fragments.ChatFragment
import com.fetlife.imetlife.Fragments.LikeFragment
import com.fetlife.imetlife.Fragments.ProfileFragment
import com.fetlife.imetlife.R
import com.fetlife.imetlife.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
runOnUiThread{
    loadFragment(HomeFragment())
    animate(binding.home)
    binding.home.setImageResource(R.drawable.group_104);
}

        binding.home.setOnClickListener {
            // Set default fragment
            runOnUiThread {
                animate(binding.home)
                binding.home.setImageResource(R.drawable.group_104);
                binding.like.setImageResource(R.drawable.group_101);
                binding.message.setImageResource(R.drawable.group_102);
                binding.profile.setImageResource(R.drawable.group_103);
                loadFragment(HomeFragment())
            }
        }

        binding.like.setOnClickListener {
            runOnUiThread {
                animate(binding.like)
                binding.home.setImageResource(R.drawable.group_100);
                binding.like.setImageResource(R.drawable.group_105);
                binding.message.setImageResource(R.drawable.group_102);
                binding.profile.setImageResource(R.drawable.group_103);
                loadFragment(LikeFragment())
            }
        }

        binding.message.setOnClickListener {
            runOnUiThread{
                animate(binding.message)
                binding.home.setImageResource(R.drawable.group_100);
                binding.like.setImageResource(R.drawable.group_101);
                binding.message.setImageResource(R.drawable.group_106);
                binding.profile.setImageResource(R.drawable.group_103);
                loadFragment(ChatFragment())
            }

        }

        binding.profile.setOnClickListener {
            runOnUiThread{
                animate(binding.profile)
                binding.home.setImageResource(R.drawable.group_100);
                binding.like.setImageResource(R.drawable.group_101);
                binding.message.setImageResource(R.drawable.group_102);
                binding.profile.setImageResource(R.drawable.group_107);
                loadFragment(ProfileFragment())
            }

        }


    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // Get the currently active fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        if (currentFragment != null) {
            // Detach the current fragment to stop its lifecycle
            transaction.detach(currentFragment)
        }

        // Add or replace the new fragment
        if (supportFragmentManager.fragments.contains(fragment)) {
            transaction.attach(fragment) // Reattach if it already exists
        } else {
            transaction.replace(R.id.nav_host_fragment, fragment)
        }

        transaction.commitNowAllowingStateLoss() // Commit safely to avoid pending operations
    }


    private fun animate(view: View) {
        runOnUiThread {
            view.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(100)
                .withEndAction {
                    // Scale back to normal
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }

    }
    override fun onBackPressed() {
        // Show the exit dialog instead of closing the activity
        showExitDialog()
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit App")
        builder.setMessage("Are you sure you want to exit?")

        // Option to close the app
        builder.setPositiveButton("Exit") { dialog, _ ->
            dialog.dismiss()
            finishAffinity() // Close all activities and exit the app
        }

        // Option to stay in the app
        builder.setNegativeButton("Stay") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog and keep the app running
        }

        builder.setCancelable(false) // Prevent dialog from being dismissed by tapping outside
        val dialog = builder.create()
        dialog.show()
    }


}
