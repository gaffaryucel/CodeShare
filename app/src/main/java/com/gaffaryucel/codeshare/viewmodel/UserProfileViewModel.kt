package com.gaffaryucel.codeshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val ref = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference

    private var _user = MutableLiveData<Resource<UserModel>>()
    val user : LiveData<Resource<UserModel>>
        get() = _user


    fun getUserProfileInfo(userId : String)= viewModelScope.launch {
        val databaseQuery = ref.child("users")
            .orderByKey()
            .equalTo(userId)
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