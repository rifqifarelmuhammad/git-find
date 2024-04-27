package bangkit.kiki.gitfind.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.kiki.gitfind.data.api.ApiConfig
import bangkit.kiki.gitfind.data.data_class.GetFollowResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFragmentViewModel: ViewModel() {
    private val _follows = MutableLiveData<List<GetFollowResponse>>()
    val follows: LiveData<List<GetFollowResponse>> = _follows

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    fun getUserFollowers(username: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getUserFollowers(username)
        client.enqueue(object : Callback<List<GetFollowResponse>> {
            override fun onResponse(
                call: Call<List<GetFollowResponse>>,
                response: Response<List<GetFollowResponse>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _follows.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<GetFollowResponse>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
                t.message?.let { Log.e("FOLLOW_FRAGMENT_VM", it) }
            }
        })
    }

    fun getUserFollowing(username: String) {
        _isLoading.value = true
        _isError.value = false

        val client = ApiConfig.getApiService().getUserFollowing(username)
        client.enqueue(object : Callback<List<GetFollowResponse>> {
            override fun onResponse(
                call: Call<List<GetFollowResponse>>,
                response: Response<List<GetFollowResponse>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _follows.value = response.body()
                } else {
                    _isError.value = true
                }
            }

            override fun onFailure(call: Call<List<GetFollowResponse>>, t: Throwable) {
                _isLoading.value = false
                _isError.value = true
            }
        })
    }
}