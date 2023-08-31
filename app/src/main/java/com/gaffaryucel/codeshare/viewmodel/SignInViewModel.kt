package com.gaffaryucel.codeshare.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class SignInViewModel : ViewModel() {
    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user

    private val auth = FirebaseAuth.getInstance()
    private var currentUser : FirebaseUser? = auth.currentUser

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() =viewModelScope.launch{
        delay(2000)
        if(currentUser != null){
            _user.value = UserModel("user")
        }else{
            _user.value = null
        }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val user = UserModel()
                        user.id = firebaseUser.uid
                        user.name = email.substringBefore("@")
                        user.email = email
                        _user.value = user
                    }
                } else {
                    _user.value = null
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}