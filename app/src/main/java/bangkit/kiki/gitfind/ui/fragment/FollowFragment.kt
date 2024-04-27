package bangkit.kiki.gitfind.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.kiki.gitfind.data.data_class.GetFollowResponse
import bangkit.kiki.gitfind.data.data_class.UserCard
import bangkit.kiki.gitfind.databinding.FragmentFollowBinding
import bangkit.kiki.gitfind.ui.adapter.ListUserAdapter
import bangkit.kiki.gitfind.view_model.FollowFragmentViewModel

class FollowFragment : Fragment() {
    companion object {
        const val ARG_TYPE = "arg_type"
        const val ARG_USERNAME = "arg_username"
    }

    private lateinit var binding: FragmentFollowBinding
    private var listFollows = ArrayList<UserCard>()
    private lateinit var followFragmentViewModel: FollowFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFollows.setHasFixedSize(true)

        val username = arguments?.getString(ARG_USERNAME) ?: ""
        val type = arguments?.getString(ARG_TYPE)

        followFragmentViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[FollowFragmentViewModel::class.java]

        if (type == "followers") followFragmentViewModel.getUserFollowers(username)
        else followFragmentViewModel.getUserFollowing(username)

        followFragmentViewModel.follows.observe(viewLifecycleOwner) { follows ->
            setFollows(follows)
        }

        followFragmentViewModel.isLoading.observe(viewLifecycleOwner) {showLoading(it)}

        followFragmentViewModel.isError.observe(viewLifecycleOwner) {showToastError(it)}
    }

    private fun setFollows(follows: List<GetFollowResponse>) {
        val tempListFollows = ArrayList<UserCard>()
        for (i in follows.indices) {
            val profilePicture: String = follows[i].avatarUrl ?: ""
            val username: String = follows[i].login ?: ""
            val userCard = UserCard(profilePicture, username)
            tempListFollows.add(userCard)
        }

        listFollows = tempListFollows
        showRecyclerList()
    }

    private fun showRecyclerList() {
        binding.rvFollows.visibility = View.VISIBLE
        binding.rvFollows.layoutManager = LinearLayoutManager(context)
        binding.rvFollows.adapter = ListUserAdapter(listFollows, false)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mainProgressBar.visibility = View.VISIBLE
        } else {
            binding.mainProgressBar.visibility = View.GONE
        }
    }

    private fun showToastError(isError: Boolean) {
        if (!isError) return

        Toast.makeText(context, "An error occurred while fetching the data", Toast.LENGTH_SHORT).show()
        binding.mainProgressBar.visibility = View.GONE
        binding.rvFollows.visibility = View.GONE
    }
}