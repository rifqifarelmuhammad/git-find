package bangkit.kiki.gitfind.data.data_class

import com.google.gson.annotations.SerializedName

data class SearchUsersByUsernameResponse(

	@field:SerializedName("total_count")
	val totalCount: Int? = null,

	@field:SerializedName("incomplete_results")
	val incompleteResults: Boolean? = null,

	@field:SerializedName("items")
	val items: List<Users>
)
