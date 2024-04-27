package bangkit.kiki.gitfind.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import bangkit.kiki.gitfind.repository.FavoriteUserRepository

class FavoriteUserViewModel(application: Application) : ViewModel() {
    private val mFavoriteUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    fun getAllFavoriteUsers() = mFavoriteUserRepository.getAllFavoriteUsers()
}