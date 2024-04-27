package bangkit.kiki.gitfind.data.api

import bangkit.kiki.gitfind.data.data_class.GetFollowResponse
import bangkit.kiki.gitfind.data.data_class.GetUserDetailResponse
import bangkit.kiki.gitfind.data.data_class.SearchUsersByUsernameResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("search/users")
    fun searchUsersByUsername(@Query("q") q: String): Call<SearchUsersByUsernameResponse>

    @GET("users/{username}")
    fun getUserDetail(@Path("username") username: String): Call<GetUserDetailResponse>

    @GET("users/{username}/followers")
    fun getUserFollowers(@Path("username") username: String): Call<List<GetFollowResponse>>

    @GET("users/{username}/following")
    fun getUserFollowing(@Path("username") username: String): Call<List<GetFollowResponse>>
}