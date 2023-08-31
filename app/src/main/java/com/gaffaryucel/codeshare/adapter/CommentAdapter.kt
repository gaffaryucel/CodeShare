package com.gaffaryucel.codeshare.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.RowCommentBinding
import com.gaffaryucel.codeshare.databinding.RowPostBinding
import com.gaffaryucel.codeshare.model.CommentModel
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.ui.home.HomeFragmentDirections
import com.gaffaryucel.codeshare.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference
    private val auth = FirebaseAuth.getInstance()

    private val diffUtil = object : DiffUtil.ItemCallback<CommentModel>() {
        override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var commentList: List<CommentModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = RowCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.binding.commentRowCommentText.text = comment.commentText
        getUserInfo(comment.commentOwner.toString(),holder)
    }
    private fun getUserInfo(commentOwner : String,holder: CommentViewHolder) {
        val databaseQuery = databaseReference.child("users")
            .orderByKey()
            .equalTo(commentOwner)
        databaseQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val s = i.getValue(UserModel::class.java)
                    if (s != null) {
                        Glide.with(holder.itemView.context)
                            .load(s.profileImageUrl)
                            .into(holder.binding.commentRowImageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(holder.itemView.context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class CommentViewHolder(val binding: RowCommentBinding) : RecyclerView.ViewHolder(binding.root)
}
