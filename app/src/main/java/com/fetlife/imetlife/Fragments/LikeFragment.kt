package com.fetlife.imetlife.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.fetlife.imetlife.Adapter.NearbyProfileAdapter
import com.fetlife.imetlife.Adapter.ProfileAdapter
import com.fetlife.imetlife.Firebase.FetchProfiles
import com.fetlife.imetlife.Model.Profile
import com.fetlife.imetlife.R
import com.fetlife.imetlife.Utils.SaveUserDetails
import com.fetlife.imetlife.databinding.FragmentLikeBinding
import kotlin.math.abs

class LikeFragment : Fragment() {

    private var _binding: FragmentLikeBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var nearbyProfileAdapter: NearbyProfileAdapter
    private val profiles = mutableListOf<Profile>()
    private var currentPage = 1
    private val itemsPerPage = 1000
    private var isAutomaticLoaded = false
    private var isNearbyLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialViewState()
        setupClickListeners()
        loadAutomaticProfiles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    private fun setupInitialViewState() {
        setAutomaticState()
        binding.viewPagerProfiles.visibility = View.VISIBLE
        binding.recyclerview.visibility = View.GONE
    }

    private fun setupClickListeners() {
        binding.automatic.setOnClickListener {
            setAutomaticState()
            binding.viewPagerProfiles.visibility = View.VISIBLE
            binding.recyclerview.visibility = View.GONE
            loadAutomaticProfiles()
        }

        binding.nearProfile.setOnClickListener {
            setNearbyState()
            binding.recyclerview.visibility = View.VISIBLE
            binding.viewPagerProfiles.visibility = View.GONE
            loadNearbyProfiles()
        }
    }

    private fun setAutomaticState() {
        updateButtonState(
            selected = binding.automatic,
            unselected = binding.nearProfile
        )
    }

    private fun setNearbyState() {
        updateButtonState(
            selected = binding.nearProfile,
            unselected = binding.automatic
        )
    }

    private fun updateButtonState(selected: View, unselected: View) {
        val selectColor = ContextCompat.getColorStateList(requireContext(), R.color.pink)
        val unselectColor =
            ContextCompat.getColorStateList(requireContext(), R.color.like_selector_color)
        selected.backgroundTintList = selectColor
        unselected.backgroundTintList = unselectColor
    }

    private fun loadAutomaticProfiles() {
        if (isAutomaticLoaded) return

        profileAdapter = ProfileAdapter(profiles, requireContext(), binding.viewPagerProfiles)
        binding.viewPagerProfiles.adapter = profileAdapter

        binding.viewPagerProfiles.setPageTransformer { page, position ->
            when {
                position < -1 -> page.alpha = 0f
                position <= 1 -> {
                    val scale = 1 - abs(position)
                    page.scaleX = scale
                    page.scaleY = scale
                }

                else -> page.alpha = 0f
            }
        }

        fetchProfiles { newProfiles ->
            newProfiles?.let {
                profileAdapter.addProfiles(it)
                currentPage++
            }
            isAutomaticLoaded = true
        }
    }

    private fun fetchProfiles(onResult: (List<Profile>?) -> Unit) {
        // Safety check for fragment lifecycle and binding initialization
        if (!isAdded || _binding == null) return // Avoid operations if fragment is not attached

        binding.progressBar.visibility = View.VISIBLE

        val userDetails = SaveUserDetails().getUserDetailsLocally(requireContext())
        val genderFilter = if (userDetails["find"] == "Guy") "Male" else "Female"

        FetchProfiles().fetchProfiles(
            genderFilter = genderFilter,
            limit = itemsPerPage,
            page = currentPage
        ) { profiles, error ->
            // Check if binding is still available before updating UI components
            if (!isAdded || _binding == null) return@fetchProfiles // Avoid operations if fragment is not attached

            binding.progressBar.visibility = View.GONE
            if (error != null) {
                Log.e("FetchProfiles", "Error: $error")
            } else {
                // Shuffle the profiles to display them in random order
                val shuffledProfiles = profiles?.shuffled()
                onResult(shuffledProfiles)
            }
        }
    }

    private fun loadNearbyProfiles() {
        if (isNearbyLoaded) return

        nearbyProfileAdapter = NearbyProfileAdapter(mutableListOf(), requireContext())
        binding.recyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerview.adapter = nearbyProfileAdapter

        fetchProfiles { newProfiles ->
            newProfiles?.let {
                nearbyProfileAdapter.addProfiles(it)
            }
            isNearbyLoaded = true
        }
    }

}

