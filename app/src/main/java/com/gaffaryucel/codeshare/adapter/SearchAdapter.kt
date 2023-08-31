package com.gaffaryucel.codeshare.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.databinding.RowSearchBinding
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.ui.dashboard.DashboardFragmentDirections

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var userList: List<UserModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = RowSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.UserName.text = user.name
        Glide.with(holder.itemView.context)
            .load(user.profileImageUrl)
            .into(holder.binding.profileImage)
        
        holder.itemView.setOnClickListener{
            val action = DashboardFragmentDirections.actionNavigationDashboardToUserProfileFragment(user.id.toString())
            Navigation.findNavController(it).navigate(action)
        }
        holder.binding.followButton.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class SearchViewHolder(val binding: RowSearchBinding) : RecyclerView.ViewHolder(binding.root)
}
