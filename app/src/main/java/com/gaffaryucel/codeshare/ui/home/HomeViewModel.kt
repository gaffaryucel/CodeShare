package com.gaffaryucel.codeshare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.Stories
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel : ViewModel() {

    // TODO: Implement the ViewModel
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val ref = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference
    private val postQuery = ref.child("users")
        .child(userId)
        .child("posts")


    private var _posts = MutableLiveData<List<PostModel>>()
    val posts : LiveData<List<PostModel>>
        get() = _posts

    private var _stories = MutableLiveData<List<Stories>>()
    val stories : LiveData<List<Stories>>
        get() = _stories

    private var _users = MutableLiveData<List<String>>()
        val users : LiveData<List<String>>
        get() = _users

    init {
        getPosts()
        getAllUsersId()
    }

    fun getPosts() = viewModelScope.launch {
        postQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("a")
                val postListr = mutableListOf<PostModel>()
                for (i in snapshot.children){
                    val value : PostModel?  = i.getValue(PostModel::class.java)
                    if (value != null){
                        postListr.add(value)
                    }
                }
                _posts.postValue(postListr)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getStories(users : List<String>) = viewModelScope.launch {
        val storyList = ArrayList<Stories>()
        runBlocking {
            for (i in 0 until users.size){
                val storyQuery = ref.child("users")
                    .child(users.get(i))
                    .child("stories")
                storyQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (s in snapshot.children){
                            val value : Stories?  = s.getValue(Stories::class.java)
                            if (value != null){
                                storyList.add(value)
                                println("storyId : "+value.storyId)
                            }
                            _stories.postValue(storyList)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }

        }
    }
    private fun getAllUsersId() = viewModelScope.launch {
        val searchQuery = ref.child("users")
        searchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<String>()
                for (i in snapshot.children){
                    val value  = i.getValue(UserModel::class.java)
                    if (value != null){
                        userList.add(value.id!!)
                    }
                }
                _users.value = userList
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}