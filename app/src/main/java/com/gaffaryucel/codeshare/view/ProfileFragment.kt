package com.gaffaryucel.codeshare.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.FragmentProfileBinding
import com.gaffaryucel.codeshare.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding : FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        observeLiveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState )
        binding.editProfilePhoto.setOnClickListener{
            val action = ProfileFragmentDirections.actionNavigationProfileToChangeProfileInfosFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }
    private fun observeLiveData(){
        viewModel.user.observe(viewLifecycleOwner){
            val userinfo = it.data
            Glide.with(requireContext()).load(userinfo?.profileImageUrl).into(binding.userProfileImage)
        }
    }
}