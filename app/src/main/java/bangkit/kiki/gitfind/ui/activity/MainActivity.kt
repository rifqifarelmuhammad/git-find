package bangkit.kiki.gitfind.ui.activity

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.kiki.gitfind.R
import bangkit.kiki.gitfind.data.data_class.UserCard
import bangkit.kiki.gitfind.data.data_class.Users
import bangkit.kiki.gitfind.data.data_store.SettingPreferences
import bangkit.kiki.gitfind.data.data_store.dataStore
import bangkit.kiki.gitfind.databinding.ActivityMainBinding
import bangkit.kiki.gitfind.ui.adapter.ListUserAdapter
import bangkit.kiki.gitfind.view_model.MainViewModel
import bangkit.kiki.gitfind.view_model.ViewModelFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private const val DEBOUNCE_DELAY = 500L
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var mainViewModel: MainViewModel
    private var listUser = ArrayList<UserCard>()
    private var debounceRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppBarMenu()

        binding.rvUsers.setHasFixedSize(true)

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        handler = Handler(Looper.getMainLooper())

        val pref = SettingPreferences.getInstance(application.dataStore)
        val factory = ViewModelFactory.getInstance(this@MainActivity.application, pref)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        mainViewModel.listUser.observe(this) { users ->
            if (users.isNotEmpty()) setListUser(users)
        }

        mainViewModel.isLoading.observe(this) {showLoading(it)}

        mainViewModel.isError.observe(this) {showToastError(it)}

        onChangedUsername()
    }

    private fun onChangedUsername() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                debounceRunnable?.let { handler.removeCallbacks(it) }

                debounceRunnable = Runnable {
                    val username = newText.toString()
                    if (username.trim() != "") {
                        binding.tvEnterUsername.visibility = View.GONE
                        val usernameHistory = mainViewModel.usernameHistory.value ?: ""
                        if (usernameHistory != username) {
                            mainViewModel.setUsernameHistory(username)
                            mainViewModel.searchUsersByUsername(username)
                        }
                    } else {
                        binding.rvUsers.visibility = View.GONE
                        binding.tvNotFound.visibility = View.GONE
                        binding.tvEnterUsername.visibility = View.VISIBLE
                    }
                }

                debounceRunnable?.let { handler.postDelayed(it, DEBOUNCE_DELAY) }

                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean = false
        })
    }

    private fun setListUser(users: List<Users>) {
        val tempListUser = ArrayList<UserCard>()
        for (i in users.indices) {
            val profilePicture: String = users[i].avatarUrl ?: ""
            val username: String = users[i].login  ?: ""
            val userCard = UserCard(profilePicture, username)
            tempListUser.add(userCard)
        }

        listUser = tempListUser
        showRecyclerList()
    }

    private fun showRecyclerList() {
        if (listUser.size == 0) {
            binding.tvNotFound.visibility = View.VISIBLE
            return
        }

        binding.rvUsers.visibility = View.VISIBLE

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.rvUsers.layoutManager = LinearLayoutManager(this)
        } else {
            binding.rvUsers.layoutManager = GridLayoutManager(this, 2)
        }

        val listUserAdapter = ListUserAdapter(listUser, true)
        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserCard) {
                val moveIntent = Intent(this@MainActivity, UserDetailActivity::class.java)
                moveIntent.putExtra(UserDetailActivity.EXTRA_USERNAME, data.username)
                startActivity(moveIntent)
            }
        })

        binding.rvUsers.adapter = listUserAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.tvNotFound.visibility = View.GONE
            binding.rvUsers.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToastError(isError: Boolean) {
        if (!isError) return

        Toast.makeText(this, "An error occurred while fetching the data", Toast.LENGTH_SHORT).show()
        binding.tvNotFound.visibility = View.GONE
        binding.rvUsers.visibility = View.GONE
    }

    private fun setupAppBarMenu() {
        binding.appBar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.favorite_user_menu -> {
                    val favoriteUsersIntent = Intent(this@MainActivity, FavoriteUserActivity::class.java)
                    startActivity(favoriteUsersIntent)
                    true
                }

                R.id.setting_menu -> {
                    val settingIntent = Intent(this@MainActivity, SettingActivity::class.java)
                    startActivity(settingIntent)
                    true
                }

                else -> false
            }
        }
    }
}