package bangkit.kiki.gitfind.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import bangkit.kiki.gitfind.ui.activity.UserDetailActivity
import bangkit.kiki.gitfind.ui.fragment.FollowFragment

class UserDetailPagerAdapter(private val activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val username = activity.intent.getStringExtra(UserDetailActivity.EXTRA_USERNAME)
        val fragment: Fragment = FollowFragment()

        fragment.arguments = Bundle().apply {
            when(position) {
                0 -> putString(FollowFragment.ARG_TYPE, "followers")
                else -> putString(FollowFragment.ARG_TYPE, "following")
            }

            putString(FollowFragment.ARG_USERNAME, username)
        }

        return fragment
    }

}