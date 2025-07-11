import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adultfriendbella.adultfre.Adapter.ProfileAdapter
import com.adultfriendbella.adultfre.Firebase.FetchProfiles
import com.adultfriendbella.adultfre.Model.Profile
import com.adultfriendbella.adultfre.Utils.SaveUserDetails
import com.adultfriendbella.adultfre.databinding.FragmentHomeBinding
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProfileAdapter
    private val profiles = mutableListOf<Profile>()
    private var currentPage = 1
    private val itemsPerPage = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize adapter and set it to ViewPager2
        adapter = ProfileAdapter(profiles, requireContext(), binding.viewPagerProfiles)
        binding.viewPagerProfiles.adapter = adapter

        // Set up page transformation animations
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

        // Load profiles
        loadProfiles()
    }

    private fun loadProfiles() {
        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE
        val userDetails = SaveUserDetails().getUserDetailsLocally(requireContext());
        val find = userDetails["find"]
        var refile = "";
        if(find=="Guy"){
         refile = "Male"
        }else{
            refile = "Female"
        }
        val fetchProfiles = FetchProfiles()
        fetchProfiles.fetchProfiles(
            genderFilter = refile.toString(),  // Modify this filter dynamically if needed
            limit = itemsPerPage,
            page = currentPage
        ) { newProfiles, error ->
            // Ensure fragment is in a valid state before updating the UI
            if (!isAdded || _binding == null) return@fetchProfiles

            if (error != null) {
                Log.e("FetchProfiles", "Error: $error")
            } else {
                newProfiles?.let {
                    adapter.addProfiles(it) // Add fetched profiles to the adapter
                    currentPage++ // Increment page number for the next fetch
                    Log.d("specialHarsh", "Page: $currentPage")
                    Log.d("specialHarsh", "Profiles: $it")
                }
            }
            binding.progressBar.visibility = View.GONE // Hide progress bar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullifying binding to avoid memory leaks
    }
}
