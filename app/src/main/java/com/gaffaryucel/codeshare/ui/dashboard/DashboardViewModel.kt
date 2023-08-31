package com.gaffaryucel.codeshare.ui.dashboard

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val ref = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference
    private val searchQuery = ref.child("users")


    private var _user = MutableLiveData<List<UserModel>>()
    val user : LiveData<List<UserModel>>
        get() = _user

    private var _queryList = MutableLiveData<List<UserModel>>()
    val queryList : LiveData<List<UserModel>>
        get() = _queryList

    init {
        getAllUser()
    }
    private fun getAllUser() = viewModelScope.launch {
        searchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<UserModel>()
                for (i in snapshot.children){
                    val value  = i.getValue(UserModel::class.java)
                    if (value != null){
                        userList.add(value)
                    }
                }
                _user.value = userList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun searchUser(searchQuery : String) = viewModelScope.launch{
        _queryList.value = _user.value!!.filter { userModel ->
            userModel.name?.contains(searchQuery, ignoreCase = true) == true
        }.toMutableList()
    }
}