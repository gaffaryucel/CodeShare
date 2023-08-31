package com.gaffaryucel.codeshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.gaffaryucel.codeshare.util.Util.DATABASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private val ref = FirebaseDatabase.getInstance(DATABASE_URL).reference
    private val databaseQuery = ref.child("users")
            .orderByKey()
            .equalTo(userId)

    private var _user = MutableLiveData<Resource<UserModel>>()
    val user : LiveData<Resource<UserModel>>
        get() = _user

    init {
        getUserProfileInfo()
    }

    private fun getUserProfileInfo()= viewModelScope.launch {
        _user.value = Resource.loading(null)
        databaseQuery.addListenerForSingleValueEvent(object : ValueEventListener {
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
}