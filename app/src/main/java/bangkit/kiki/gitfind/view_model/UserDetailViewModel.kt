package bangkit.kiki.gitfind.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.kiki.gitfind.data.api.ApiConfig
import bangkit.kiki.gitfind.data.data_class.GetUserDetailResponse
import bangkit.kiki.gitfind.data.entity.FavoriteUser
import bangkit.kiki.gitfind.repository.FavoriteUserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel(application: Application): ViewModel() {
    private val _user = MutableLiveData<GetUserDetailResponse>()
    val user: LiveData<GetUserDetailResponse> = _user

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val mFavoriteUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    fun getUserDetail(username: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getUserDetail(username)
        client.enqueue(object : Callback<GetUserDetailResponse> {
            override fun onResponse(
                call: Call<GetUserDetailResponse>,
                response: Response<GetUserDetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<GetUserDetailResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }

    fun getFavoriteUserByUsername(username: String) = mFavoriteUserRepository.getFavoriteUserByUsername(username)

    fun addFavoriteUser(favoriteUser: FavoriteUser) = mFavoriteUserRepository.insert(favoriteUser)

    fun removeFavoriteUser(favoriteUser: FavoriteUser) = mFavoriteUserRepository.delete(favoriteUser)
}