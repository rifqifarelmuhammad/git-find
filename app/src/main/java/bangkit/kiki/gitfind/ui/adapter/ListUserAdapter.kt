package bangkit.kiki.gitfind.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bangkit.kiki.gitfind.data.data_class.UserCard
import bangkit.kiki.gitfind.databinding.UserCardBinding
import com.bumptech.glide.Glide

class ListUserAdapter(private val listUser: ArrayList<UserCard>, private val isClickable: Boolean): RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(var binding: UserCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = UserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (profilePicture, username) = listUser[position]
        Glide.with(holder.itemView.context).load(profilePicture).into(holder.binding.profilePicture)
        holder.binding.username.text = username

        if (isClickable) holder.itemView.setOnClickListener{ onItemClickCallback.onItemClicked(listUser[holder.adapterPosition]) }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun getItemCount(): Int = listUser.size

    interface OnItemClickCallback {
        fun onItemClicked(data: UserCard)
    }
}