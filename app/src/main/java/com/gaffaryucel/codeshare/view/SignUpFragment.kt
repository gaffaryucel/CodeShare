package com.gaffaryucel.codeshare.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.FragmentSignInBinding
import com.gaffaryucel.codeshare.databinding.FragmentSignUpBinding
import com.gaffaryucel.codeshare.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding : FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        binding.signUpButton.setOnClickListener {
            val name = binding.nameEdittext.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.signUp(name,email, password)
        }
        binding.goToSignInFragment.setOnClickListener{
            findNavController().popBackStack()
        }

        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val intent = Intent(requireActivity(),HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}