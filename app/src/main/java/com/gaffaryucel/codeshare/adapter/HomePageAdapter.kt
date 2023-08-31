package com.gaffaryucel.codeshare.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.RowPostBinding
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.ui.home.HomeFragmentDirections
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomePageAdapter : RecyclerView.Adapter<HomePageAdapter.PostViewHolder>() {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference
    private val auth = FirebaseAuth.getInstance()

    private val diffUtil = object :DiffUtil.ItemCallback<PostModel>(){
        override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            return oldItem == newItem
        }
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)

    var postList: List<PostModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = RowPostBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        var liked = false
        val postforUser = postList[position]
        var likes = postforUser.likes?.size ?: 0

        getUserPhoto(postforUser.userId.toString(),holder)

        holder.binding.apply {
            post = postforUser
        }

        Glide.with(holder.itemView.context)
            .load(postforUser.content.toString())
            .into(holder.binding.postIv)

        holder.binding.likeCount.text = likes.toString()

        holder.binding.like.setOnClickListener{
            if (liked){
                removeLike(postforUser,holder)
                liked = false
                likes -= 1
                holder.binding.likeCount.text = likes.toString()
            }else{
                likePoest(postforUser,holder)
                liked = true
                likes += 1
                holder.binding.likeCount.text = likes.toString()
            }
        }
        if (postforUser.likes != null){
            if (postforUser.likes!!.contains(auth.currentUser!!.uid)){
                holder.binding.like.setImageResource(R.drawable.ic_favorite_heart)
                liked = true
            }
        }
        holder.binding.commentImageView.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToCommentFragment(postforUser.postId.toString(),postforUser.userId.toString())
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class PostViewHolder(val binding: RowPostBinding) : RecyclerView.ViewHolder(binding.root)

    private fun likePoest(post : PostModel,holder: PostViewHolder){
        val userPhotoRef = databaseReference.child("users").child(post.userId.toString())
            .child("posts")
            .child(post.postId.toString()).child("likes").child(auth.currentUser!!.uid)
        userPhotoRef.setValue(auth.currentUser!!.uid).addOnCompleteListener {
            if (it.isSuccessful){
                holder.binding.like.setImageResource(R.drawable.ic_favorite_heart)
            }else{
                Toast.makeText(holder.itemView.context, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeLike(post : PostModel,holder: PostViewHolder){
        val userPhotoRef = databaseReference.child("users").child(post.userId.toString())
            .child("posts")
            .child(post.postId.toString()).child("likes").child(auth.currentUser!!.uid)
        userPhotoRef.removeValue(object : DatabaseReference.CompletionListener{
            override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                holder.binding.like.setImageResource(R.drawable.ic_heart)
            }
        })
    }

    private fun getUserPhoto(postOwner : String,holder: PostViewHolder){
        val databaseQuery = databaseReference.child("users")
            .orderByKey()
            .equalTo(postOwner)
        databaseQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("a")
                for (i in snapshot.children){
                    val s = i.getValue(UserModel::class.java)
                    if (s != null){
                        Glide.with(holder.itemView.context)
                            .load(s.profileImageUrl)
                            .into(holder.binding.profileIv)
                        holder.binding.ownerName.text = s.name
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
