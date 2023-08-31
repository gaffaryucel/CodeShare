package com.gaffaryucel.codeshare.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.databinding.FragmentUserProfileBinding
import com.gaffaryucel.codeshare.viewmodel.UserProfileViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserProfileFragment : Fragment() {

    private lateinit var binding : FragmentUserProfileBinding
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Hiding Botrtom of the activity
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.INVISIBLE
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.INVISIBLE
        val appBar = activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)
        appBar?.visibility = View.INVISIBLE

        binding = FragmentUserProfileBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        val userId = arguments?.getString("id") ?: ""
        viewModel.getUserProfileInfo(userId)
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.user.observe(viewLifecycleOwner){
            val userinfo = it.data
            Glide.with(requireContext()).load(userinfo?.profileImageUrl).into(binding.userProfileImageView)
        }
    }
    override fun onStop() {
        super.onStop()

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.VISIBLE

        // veya Floating Action Button'ı gizlemek
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.VISIBLE

        val appBar = activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)
        appBar?.visibility = View.VISIBLE

    }
}