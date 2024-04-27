package bangkit.kiki.gitfind.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import bangkit.kiki.gitfind.R
import bangkit.kiki.gitfind.data.data_store.SettingPreferences
import bangkit.kiki.gitfind.data.data_store.dataStore
import bangkit.kiki.gitfind.databinding.ActivitySettingBinding
import bangkit.kiki.gitfind.view_model.SettingViewModel
import bangkit.kiki.gitfind.view_model.ViewModelFactory

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBar.topAppBar.menu.findItem(R.id.setting_menu).isVisible = false
        binding.appBar.topAppBar.title = "Setting"

        setupAppBarMenu()

        val pref = SettingPreferences.getInstance(application.dataStore)
        val factory = ViewModelFactory.getInstance(this@SettingActivity.application, pref)
        settingViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

        val switchTheme = binding.switchTheme
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener {  _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupAppBarMenu() {
        binding.appBar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.favorite_user_menu -> {
                    val favoriteUsersIntent = Intent(this@SettingActivity, FavoriteUserActivity::class.java)
                    startActivity(favoriteUsersIntent)
                    true
                }

                else -> false
            }
        }
    }
}