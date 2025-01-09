package com.fetlifenew.imetlife.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.fetlifenew.imetlife.AccountCreation
import com.fetlifenew.imetlife.Utils.SaveUserDetails
import com.fetlifenew.imetlife.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null // Create a nullable binding variable
    private val binding get() = _binding!! // Get a non-null binding reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root // Return the root view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null || !isAdded) return

        binding.progressBar.visibility = View.VISIBLE

        // Get stored details
        val userDetails = SaveUserDetails().getUserDetailsLocally(requireContext())
        val email = userDetails["email"]
        val password = userDetails["password"]
        val username = userDetails["username"]
        val gender = userDetails["gender"]
        val age = userDetails["age"]
        val find = userDetails["find"]
        val about = userDetails["about"]
        val imageBase64 = userDetails["imageBase64"]

        // Set user details to the views
        binding.name.text = username
        binding.profileage.text = "${age} years old"
        binding.about.text = about

        // Load profile image using Picasso
        val base64Image = imageBase64 ?: ""
        loadWithPicasso(base64Image)

        // Handle link clicks
        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
        binding.pp.setOnClickListener {
            showOpenLinkDialog("https://sites.google.com/view/fetlife-datinghookup-privacy/home")
        }
        binding.tt.setOnClickListener {
            showOpenLinkDialog("https://sites.google.com/view/fetlife-datinghookup-terms/home")
        }
        binding.ugc.setOnClickListener{
            showOpenLinkDialog("https://sites.google.com/view/userss-ugc-policy/home")
        }
        binding.deleteaccount.setOnClickListener{
            showDeleteAccountDialog()
        }
    }

    private fun loadWithPicasso(url: String) {
        // Ensure binding is not null and the fragment is attached
        if (_binding == null || !isAdded) return

        val imageView = binding.webview
        val imageUrlWithCacheBust = "$url?cacheBust=${UUID.randomUUID()}"
        Picasso.get()
            .load(imageUrlWithCacheBust)
            .into(imageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    // Ensure binding is still valid after callback
                    if (_binding != null && isAdded) {
                        binding.progressBar.visibility = View.GONE
                        Log.v("main", "Picasso image loaded successfully.")
                    }
                }

                override fun onError(e: Exception?) {
                    // Ensure binding is still valid after callback
                    if (_binding != null && isAdded) {
                        binding.progressBar.visibility = View.GONE
                        Log.e("main", "Error loading image: ${e?.message}")
                    }
                }
            })
    }

    fun showOpenLinkDialog(url: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Open Link")
        builder.setMessage("Are you sure you want to open this link in the browser?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            openLinkInBrowser(url)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Account")
        builder.setMessage("Your account will enter a 15-day cooling-off period after requesting deletion. Logging in during this time will cancel the deletion. If you don't log in within 15 days, the account will be deleted and cannot be recovered.")
        builder.setPositiveButton("Yes") { dialog, _ ->
            FirebaseAuth.getInstance().signOut()
            SaveUserDetails().clearUserDetailsLocally(requireContext())
            val intent = Intent(activity, AccountCreation::class.java)
            intent.putExtra("mode", 2)
            startActivity(intent)
            activity?.finish()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
    private fun openLinkInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding to avoid memory leaks
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout App?")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Logout") { dialog, _ ->
            FirebaseAuth.getInstance().signOut()
            SaveUserDetails().clearUserDetailsLocally(requireContext())
            val intent = Intent(activity, AccountCreation::class.java)
            intent.putExtra("mode", 2)
            startActivity(intent)
            activity?.finish()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }
}
