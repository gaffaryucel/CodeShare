package com.gaffaryucel.codeshare.view

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.FragmentSignInBinding
import com.gaffaryucel.codeshare.viewmodel.SignInViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {


    private lateinit var binding : FragmentSignInBinding
    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)

        val videoPath = "android.resource://com.gaffaryucel.codeshare/${R.raw.splash}"
        val videoUri = Uri.parse(videoPath)

        binding.videoView.setVideoURI(videoUri)
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            // Videonun hazır olduğunda oynatma başlatılacak
            mediaPlayer.start()
        }


        binding.logInButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            viewModel.signIn(email, password)
        }
        binding.goToSignUp.setOnClickListener{
            val actiton = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            Navigation.findNavController(it).navigate(actiton)
        }

       observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.videoLayout.visibility = View.GONE
            if (user != null) {
                // Videonun oynatılması tamamlandığında MainActivity2'ye geçiş yapın
                val intent = Intent(requireActivity(),HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }else{
                binding.videoLayout.visibility = View.GONE
                binding.signInLayout.visibility = View.VISIBLE
            }
        }
    }
}