package com.gaffaryucel.codeshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChangeProfileInfosViewModel : ViewModel() {

    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance(Util.DATABASE_URL).reference

    private val auth = FirebaseAuth.getInstance()

    private val _uploadPhotoMessage = MutableLiveData<Resource<String>>()
    val uploadPhotoMessage : LiveData<Resource<String>> = _uploadPhotoMessage

    private val _userInfo = MutableLiveData<Resource<UserModel>>()
    val userInfo : LiveData<Resource<UserModel>>
        get() = _userInfo

    init {
        getUserProfileInfo()
    }

    fun uploadProfilePicture(r: ByteArray) = viewModelScope.launch {
        _uploadPhotoMessage.value = Resource.loading("loading")

        val photoFileName = "photo_profilePhoto.jpg"
        val photoRef = storageReference.child("users/${auth.currentUser?.uid}/profilePhoto/$photoFileName")

        photoRef.putBytes(r)
            .addOnSuccessListener {
                photoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // imageUrl, yüklenen byte dizisinin URL'sidir.

                        // Resim URL'sini veritabanına kaydet
                        val userId = auth.currentUser?.uid
                        userId?.let {
                            val userPhotoRef = databaseReference.child("users").child(userId)
                                .child("profileImageUrl")
                            userPhotoRef.setValue(imageUrl).addOnCompleteListener {
                                _uploadPhotoMessage.value = Resource.success("loaded in Database")
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // URL alınamazsa burada hata işleme kodlarınızı yazabilirsiniz.
                        _uploadPhotoMessage.value =
                            Resource.error("cannot acces url", exception.localizedMessage)
                    }
            }.addOnFailureListener { exception ->
                // Yükleme başarısız olursa, burada hata işleme kodlarınızı yazabilirsiniz.
                _uploadPhotoMessage.value =
                    Resource.error("cannot upload photo", exception.localizedMessage)
            }
    }
    private fun getUserProfileInfo(){
        databaseReference.child("users").
            orderByKey()
            .equalTo(auth.currentUser?.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val s = i.getValue(UserModel::class.java)
                    if (s != null){
                        _userInfo.value = Resource.success(s)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _userInfo.value = Resource.error(error.message,null)
            }
        })
    }
}