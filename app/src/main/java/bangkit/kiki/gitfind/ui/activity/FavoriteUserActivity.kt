package bangkit.kiki.gitfind.ui.activity

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.kiki.gitfind.R
import bangkit.kiki.gitfind.data.data_class.UserCard
import bangkit.kiki.gitfind.data.data_store.SettingPreferences
import bangkit.kiki.gitfind.data.data_store.dataStore
import bangkit.kiki.gitfind.data.entity.FavoriteUser
import bangkit.kiki.gitfind.databinding.ActivityFavoriteUserBinding
import bangkit.kiki.gitfind.ui.adapter.ListUserAdapter
import bangkit.kiki.gitfind.view_model.FavoriteUserViewModel
import bangkit.kiki.gitfind.view_model.ViewModelFactory

class FavoriteUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteUserBinding
    private lateinit var favoriteUserViewModel: FavoriteUserViewModel
    private var listFavoriteUser = ArrayList<UserCard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_user)

        binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBar.topAppBar.title = "Favorite Users"
        binding.appBar.topAppBar.menu.findItem(R.id.favorite_user_menu).isVisible = false
        setupAppBarMenu()

        binding.rvUsers.setHasFixedSize(true)

        val pref = SettingPreferences.getInstance(application.dataStore)
        val factory = ViewModelFactory.getInstance(this@FavoriteUserActivity.application, pref)
        favoriteUserViewModel = ViewModelProvider(this, factory)[FavoriteUserViewModel::class.java]

        favoriteUserViewModel.getAllFavoriteUsers().observe(this) { favoriteUsers ->
            setFavoriteUsers(favoriteUsers)
        }
    }

    private fun setFavoriteUsers(favoriteUsers: List<FavoriteUser>) {
        val tempListFavoriteUsers = ArrayList<UserCard>()
        for (i in favoriteUsers.indices) {
            val profilePicture: String = favoriteUsers[i].avatarUrl ?: ""
            val userCard = UserCard(profilePicture, favoriteUsers[i].username)
            tempListFavoriteUsers.add(userCard)
        }

        listFavoriteUser = tempListFavoriteUsers
        showRecyclerList()
    }

    private fun showRecyclerList() {
        if (listFavoriteUser.size == 0) {
            binding.tvFavoriteUsersIsEmpty.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
            return
        }

        binding.tvFavoriteUsersIsEmpty.visibility = View.GONE
        binding.rvUsers.visibility = View.VISIBLE

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
        } else {
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        }

        val listUserAdapter = ListUserAdapter(listFavoriteUser, true)
        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserCard) {
                val moveIntent = Intent(this@FavoriteUserActivity, UserDetailActivity::class.java)
                moveIntent.putExtra(UserDetailActivity.EXTRA_USERNAME, data.username)
                startActivity(moveIntent)
            }
        })

        binding.rvUsers.adapter = listUserAdapter
    }

    private fun setupAppBarMenu() {
        binding.appBar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.setting_menu -> {
                    val settingIntent = Intent(this@FavoriteUserActivity, SettingActivity::class.java)
                    startActivity(settingIntent)
                    true
                }

                else -> false
            }
        }
    }
}