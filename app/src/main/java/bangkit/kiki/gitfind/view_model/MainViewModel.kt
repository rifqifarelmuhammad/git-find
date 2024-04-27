package bangkit.kiki.gitfind.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bangkit.kiki.gitfind.data.api.ApiConfig
import bangkit.kiki.gitfind.data.data_class.SearchUsersByUsernameResponse
import bangkit.kiki.gitfind.data.data_class.Users
import bangkit.kiki.gitfind.data.data_store.SettingPreferences
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {
    private val _listUser = MutableLiveData<List<Users>>()
    val listUser: LiveData<List<Users>> = _listUser

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _usernameHistory = MutableLiveData("")
    val usernameHistory: LiveData<String> = _usernameHistory

    fun setUsernameHistory(username: String) {
        _usernameHistory.value = username
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun searchUsersByUsername(username: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().searchUsersByUsername(username)
        client.enqueue(object : Callback<SearchUsersByUsernameResponse> {
            override fun onResponse(
                call: Call<SearchUsersByUsernameResponse>,
                response: Response<SearchUsersByUsernameResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUser.value = response.body()?.items
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<SearchUsersByUsernameResponse>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}