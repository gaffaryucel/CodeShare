package com.gaffaryucel.codeshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.CommentModel
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel : ViewModel() {
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private val ref = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference

    private var _user = MutableLiveData<Resource<UserModel>>()
    val user : LiveData<Resource<UserModel>>
        get() = _user

    private var _post = MutableLiveData<Resource<PostModel>>()
    val post : LiveData<Resource<PostModel>>
        get() = _post

    private var _comments = MutableLiveData<Resource<List<CommentModel>>>()
    val comments : LiveData<Resource<List<CommentModel>>>
        get() = _comments

    fun createComment(comment : CommentModel) = viewModelScope.launch{
        val databaseQueryForUser = ref.child("users")
            .child(comment.postOwner.toString())
            .child("posts")
            .child(comment.postId.toString())
            .child("comments")
            .child(comment.commentId.toString())
            .setValue(comment).addOnSuccessListener {
                getComments(comment.postOwner.toString(),comment.postId.toString())
            }
    }
    fun getComments(userId : String,postId: String)= viewModelScope.launch{
        _comments.value = Resource.loading(null)
        val databaseQueryForUser = ref.child("users")
            .child(userId)
            .child("posts")
            .child(postId)
            .child("comments")

        databaseQueryForUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentList = ArrayList<CommentModel>()
                for (i in snapshot.children){
                    val commentsMap = i.getValue() as? Map<String, Any>

                    val commentId = commentsMap?.get("commentId") as? String
                    val commentOwner = commentsMap?.get("commentOwner") as? String
                    val postOwner = commentsMap?.get("postOwner") as? String
                    val commentText = commentsMap?.get("commentText") as? String
                    val time = commentsMap?.get("time") as? String

                    val commentModel = CommentModel(commentId, commentOwner, postOwner, commentText, time)
                    commentList.add(commentModel)
                }
                _comments.postValue(Resource.success(commentList))
            }

            override fun onCancelled(error: DatabaseError) {
                _comments.value = Resource.error(error.message,null)
            }
        })
    }

    fun getUserProfileInfo(userId : String)= viewModelScope.launch {
        val databaseQueryForUser = ref.child("users")
            .orderByKey()
            .equalTo(userId)
        _user.value = Resource.loading(null)
        databaseQueryForUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val s = i.getValue(UserModel::class.java)
                    if (s != null){
                        _user.value = Resource.success(s)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _user.value = Resource.error(error.message,null)
            }
        })
    }

    fun getPostInfo(userId : String,postId : String)= viewModelScope.launch {
        val databaseQueryForPost = ref.child("users")
            .child(userId)
            .child("posts")
            .orderByKey()
            .equalTo(postId)
        databaseQueryForPost.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val s = i.getValue(PostModel::class.java)
                    if (s != null){
                        _post.value = Resource.success(s)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _post.value = Resource.error(error.message,null)
            }
        })
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}
