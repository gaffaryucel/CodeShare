package com.gaffaryucel.codeshare.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gaffaryucel.codeshare.databinding.ActivityShareBinding
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.util.Status
import com.gaffaryucel.codeshare.viewmodel.ShareViewModel
import java.io.ByteArrayOutputStream

class ShareActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private val PERMISSION_REQUEST_CODE = 200
    private var allPermissionsGranted = false

    private var resultByteArray = byteArrayOf()

    private lateinit var binding : ActivityShareBinding
    private lateinit var viewModel: ShareViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(ShareViewModel::class.java)
        setContentView(binding.root)
        val intent = intent
        val story = intent.getStringExtra("story") ?: ""
        requestPermissionsIfNeeded()

        binding.postImageView.setOnClickListener{
            if (allPermissionsGranted){
                openCamera()
            }else{
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        binding.postImageView.setOnLongClickListener {
            if (allPermissionsGranted) {
                openGallery()
            } else {
                Toast.makeText(this@ShareActivity, "Permission not granted", Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.shareButton.setOnClickListener {
            if (story.equals("story")){
                viewModel.shareStory(resultByteArray)
            }else{
                if (resultByteArray.isNotEmpty()){
                    val post = PostModel()
                    post.description = binding.descriptionEditText.text.toString()
                    viewModel.uploadPhotoAndSaveUrl(resultByteArray, post)
                }
            }

        }
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.uploadPhotoMessage.observe(this, Observer { message->
            when(message.status){
                Status.SUCCESS->{
                    Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR->{
                    Toast.makeText(this, "Upload Faild", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                    Toast.makeText(this, "Uploading", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.postImageView.setImageBitmap(imageBitmap)
                    compressedForCam(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    binding.postImageView.setImageURI(selectedImageUri)
                    if (selectedImageUri != null){
                        compressedForGalery(selectedImageUri)
                    }
                }
            }
        }
    }
    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // İzinleri talep et
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // İzinler zaten verilmişse burada yapılacak işlemler
                allPermissionsGranted = true
            }
        } else {
            // Android 6.0 (Marshmallow) öncesi sürümlerde izin kontrolü yapmanıza gerek yok
            allPermissionsGranted = true
        }
    }

    //Kameradan gelen resmi compress etmek için kullanılan fonksiyon
    private fun compressedForCam(photo : Bitmap){
        var compress = BackgroundImageCompress(photo)
        var myUri : Uri? = null
        compress.execute(myUri)
    }
    //Galeryden gelen resmi compress etmek için kullanılan fonksiyon
    private fun compressedForGalery(photo: Uri){
        var compress = BackgroundImageCompress()
        compress.execute(photo)
    }

    //arkaplanda kompress işleminin yapılacağı sınıf
    inner class BackgroundImageCompress : AsyncTask<Uri, Void, ByteArray> {
        var myBitmap : Bitmap? = null
        //eğer kameradan görsel alınırsa burda bir bitmap değeri olur
        //ve bu değer myBitmap'e eşitlenir
        constructor(b : Bitmap?){
            if (b != null){
                myBitmap = b
            }
        }
        //eğer galeryden bir değer geldiyse bu Uri olarak verilir
        //ve bu constractor boş olarak kalır
        constructor()
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Uri?): ByteArray {
            //Galeryden resim geldi ise galerideki resnib pozisyonuna git ve bitmap değerini al
            //anlamına geliyor
            if (myBitmap == null){
                //Uri
                myBitmap = MediaStore.Images.Media.getBitmap(this@ShareActivity.contentResolver,p0[0])
            }
            var imageByteArray : ByteArray? = null
            for (i in 1..5){
                imageByteArray = converteBitmapTOByte(myBitmap,100/i)
            }
            return imageByteArray!!
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }

        //son olarak sonuçla ne yapılacağı burada belirlenir istediğiniz bir fonksiyona parametre olarak atanabilir
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            if (result!=null){
               resultByteArray = result
            }
        }
    }
    //bitmap'i byteArray'e çeviren fonksiyon
    private fun converteBitmapTOByte(myBitmap: Bitmap?, i: Int): ByteArray {
        var stream = ByteArrayOutputStream()
        myBitmap?.compress(Bitmap.CompressFormat.JPEG,i,stream)
        return stream.toByteArray()
    }
}