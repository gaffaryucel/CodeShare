package com.gaffaryucel.codeshare.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.util.Util.DATABASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class SignUpViewModel : ViewModel() {
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL)
    private val myRef : DatabaseReference = database.reference
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()


    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user

    fun signUp(name : String,email: String, password: String) = viewModelScope.launch{
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val user = UserModel()
                        user.id = it.uid
                        user.name = name
                        user.email = email
                        user.createdAt = getCurrentDateFormatted()
                        _user.value = user
                        saveUserIntoFirebase(user)
                    }
                } else {
                    _user.value = null
                }
            }
    }
    private fun saveUserIntoFirebase(user :UserModel){
        myRef.child("users").child(user.id!!).setValue(user).addOnCompleteListener {

        }
    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateFormatted(): String {
        val pattern = "dd-MM-yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat.format(Date())
    }


    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}