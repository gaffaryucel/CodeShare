package com.gaffaryucel.codeshare.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaffaryucel.codeshare.model.CommentModel
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.Stories
import com.gaffaryucel.codeshare.util.Resource
import com.gaffaryucel.codeshare.util.Util
import com.gaffaryucel.codeshare.util.Util.DATABASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShareViewModel : ViewModel() {

    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance(DATABASE_URL).reference

    private val auth = FirebaseAuth.getInstance()

    private val _uploadPhotoMessage = MutableLiveData<Resource<String>>()
    val uploadPhotoMessage : LiveData<Resource<String>> = _uploadPhotoMessage

    fun uploadPhotoAndSaveUrl(r: ByteArray,postModel : PostModel) = viewModelScope.launch {
        // Resmi Firebase Storage'a yükle
        _uploadPhotoMessage.value = Resource.loading("loading")

        val photoFileName = "photo_${System.currentTimeMillis()}.jpg"
        val photoRef = storageReference.child("users/${auth.currentUser?.uid}/posts/$photoFileName")

        photoRef.putBytes(r)
            .addOnSuccessListener {
                photoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        postModel.content = imageUrl
                        postModel.userId = auth.currentUser?.uid
                        postModel.postId = getCurrentDateTime().replace("/","-")

                        // Resim URL'sini veritabanına kaydet
                        val userId = auth.currentUser?.uid
                        userId?.let {
                            val userPhotoRef = databaseReference.child("users").child(userId)
                                .child("posts").child(postModel.postId.toString())
                            userPhotoRef.setValue(postModel).addOnCompleteListener {
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
    fun shareStory(r: ByteArray) = viewModelScope.launch {
        // Resmi Firebase Storage'a yükle
        val storyTime = getCurrentDateTime()
        _uploadPhotoMessage.value = Resource.loading("loading")

        val photoFileName = "storyPhoto${storyTime}.jpg"
        val photoRef = storageReference.child("users/${auth.currentUser?.uid}/stories/$photoFileName")
        photoRef.putBytes(r)
            .addOnSuccessListener {
                photoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // imageUrl, yüklenen byte dizisinin URL'sidir.
                        val story = Stories(storyId = storyTime, imageUrl = imageUrl)

                        // Resim URL'sini veritabanına kaydet
                        val userId = auth.currentUser?.uid
                        userId?.let {
                            val userPhotoRef = databaseReference.child("users").child(userId)
                                .child("stories").child(storyTime)
                            userPhotoRef.setValue(story).addOnCompleteListener {
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

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

}