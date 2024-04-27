package bangkit.kiki.gitfind.ui.activity

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import bangkit.kiki.gitfind.R
import bangkit.kiki.gitfind.data.data_class.GetUserDetailResponse
import bangkit.kiki.gitfind.data.data_store.SettingPreferences
import bangkit.kiki.gitfind.data.data_store.dataStore
import bangkit.kiki.gitfind.data.entity.FavoriteUser
import bangkit.kiki.gitfind.databinding.ActivityUserDetailBinding
import bangkit.kiki.gitfind.ui.adapter.UserDetailPagerAdapter
import bangkit.kiki.gitfind.view_model.UserDetailViewModel
import bangkit.kiki.gitfind.view_model.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_USERNAME = "extra_username"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }

    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var userDetailViewModel: UserDetailViewModel
    private lateinit var favoriteUser: FavoriteUser
    private var favorited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBar.topAppBar.title = "Detail User"
        setupAppBarMenu()

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.linearLayout.setPadding(0, 32, 0, 32)

        } else {
            binding.linearLayout.setPadding(0, 12, 0, 12)
        }

        val pref = SettingPreferences.getInstance(application.dataStore)
        val factory = ViewModelFactory.getInstance(this@UserDetailActivity.application, pref)
        userDetailViewModel = ViewModelProvider(this, factory)[UserDetailViewModel::class.java]

        intent.getStringExtra(EXTRA_USERNAME)?.let {
            userDetailViewModel.getUserDetail(it)
            userDetailViewModel.getFavoriteUserByUsername(it).observe(this) { user ->
                favorited = if (user == null) {
                    binding.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    false
                } else {
                    binding.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24)
                    true
                }
            }
        }

        userDetailViewModel.user.observe(this) { user ->
            setUserDetail(user)

            favoriteUser = FavoriteUser(
                username = user.login!!,
                avatarUrl = user.avatarUrl!!
            )

            binding.shareButton.setOnClickListener{
                val url = user.htmlUrl!!
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(shareIntent, "Share using..."))
            }
        }

        userDetailViewModel.isLoading.observe(this) {showLoading(it)}

        userDetailViewModel.isError.observe(this) {showToastError(it)}

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = UserDetailPagerAdapter(this)
        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        binding.favoriteButton.setOnClickListener {
            if (favorited) userDetailViewModel.removeFavoriteUser(favoriteUser)
            else userDetailViewModel.addFavoriteUser(favoriteUser)
        }
    }

    private fun setUserDetail(user: GetUserDetailResponse) {
        val username = "@${user.login}"
        val followers = "${user.followers} followers"
        val following = "${user.following} following"

        Glide.with(this).load(user.avatarUrl).into(binding.profilePicture)
        binding.name.text = user.name
        binding.username.text = username
        binding.followers.text = followers
        binding.following.text = following
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mainProgressBar.visibility = View.VISIBLE
        } else {
            binding.mainLayout.visibility = View.VISIBLE
            binding.mainProgressBar.visibility = View.GONE
        }
    }

    private fun showToastError(isError: Boolean) {
        if (!isError) return

        Toast.makeText(this, "An error occurred while fetching the data", Toast.LENGTH_SHORT).show()
        binding.mainLayout.visibility = View.GONE
        binding.mainProgressBar.visibility = View.GONE
    }

    private fun setupAppBarMenu() {
        binding.appBar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.favorite_user_menu -> {
                    val moveIntent = Intent(this@UserDetailActivity, FavoriteUserActivity::class.java)
                    startActivity(moveIntent)
                    true
                }

                R.id.setting_menu -> {
                    val settingIntent = Intent(this@UserDetailActivity, SettingActivity::class.java)
                    startActivity(settingIntent)
                    true
                }

                else -> false
            }
        }
    }
}