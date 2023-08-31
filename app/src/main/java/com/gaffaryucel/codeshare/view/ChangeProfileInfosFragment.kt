package com.gaffaryucel.codeshare.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.ActivityShareBinding
import com.gaffaryucel.codeshare.databinding.FragmentChangeProfileInfosBinding
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.util.Status
import com.gaffaryucel.codeshare.viewmodel.ChangeProfileInfosViewModel
import java.io.ByteArrayOutputStream

class ChangeProfileInfosFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 101
    private val REQUEST_IMAGE_PICK = 102
    private val PERMISSION_REQUEST_CODE = 200
    private var allPermissionsGranted = false

    private var resultByteArray = byteArrayOf()

    private lateinit var binding : FragmentChangeProfileInfosBinding
    private lateinit var viewModel: ChangeProfileInfosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeProfileInfosBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChangeProfileInfosViewModel::class.java)

        requestPermissionsIfNeeded()

        binding.profileImageView.setOnClickListener{
            if (allPermissionsGranted){
                openCamera()
            }else{
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
            }
        }
        binding.profileImageView.setOnLongClickListener {
            if (allPermissionsGranted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission not granted", Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.shareButton.setOnClickListener {
            if (resultByteArray.isNotEmpty()){
                viewModel.uploadProfilePicture(resultByteArray)
            }
        }
        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.uploadPhotoMessage.observe(viewLifecycleOwner, Observer { message->
            when(message.status){
                Status.SUCCESS->{
                    Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT).show()
                }
                Status.ERROR->{
                    Toast.makeText(requireContext(), "Upload Faild", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                    Toast.makeText(requireContext(), "Uploading", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
            when(userInfo.status){
                Status.SUCCESS->{
                    val userData = userInfo.data
                    if (userData!=null){
                        binding.apply {
                            user = userData
                        }
                        Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.profileImageView)
                    }
                }
                Status.ERROR->{
                    Toast.makeText(requireContext(), "Upload Faild", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                    Toast.makeText(requireContext(), "Uploading", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
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
                    binding.profileImageView.setImageBitmap(imageBitmap)
                    compressedForCam(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    binding.profileImageView.setImageURI(selectedImageUri)
                    if (selectedImageUri != null){
                        compressedForGalery(selectedImageUri)
                    }
                }
            }
        }
    }
    private fun requestPermissionsIfNeeded() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // İzinleri talep et
            ActivityCompat.requestPermissions(
                requireActivity(),
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
                myBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,p0[0])
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