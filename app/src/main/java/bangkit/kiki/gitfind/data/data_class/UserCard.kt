package bangkit.kiki.gitfind.data.data_class

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserCard(
    val profilePicture: String,
    val username: String
): Parcelable
