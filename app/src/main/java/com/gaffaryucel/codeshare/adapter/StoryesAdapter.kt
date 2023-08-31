package com.gaffaryucel.codeshare.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.RowStoryBinding
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.Stories

class StoryAdapter() : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private val diffUtil = object : DiffUtil.ItemCallback<Stories>(){
        override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
            return oldItem == newItem
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var storiesList: List<Stories>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storiesList[position]
        Glide.with(holder.itemView.context)
            .load(story.imageUrl)
            .into(holder.binding.imageStore)
        holder.binding.UserName.text = "asdasdasdasd"
    }

    override fun getItemCount(): Int {
        return storiesList.size
    }

    inner class ViewHolder(val binding : RowStoryBinding) : RecyclerView.ViewHolder(binding.root)
}
