package com.gaffaryucel.codeshare.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaffaryucel.codeshare.adapter.HomePageAdapter
import com.gaffaryucel.codeshare.adapter.SearchAdapter
import com.gaffaryucel.codeshare.databinding.FragmentDashboardBinding
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.ui.home.HomeViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel

    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter()

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchUser(query.toString())
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchUser(newText.toString())
                return true
            }
        }
        )

        observeLiveData()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observeLiveData() {
        viewModel.user.observe(viewLifecycleOwner) {
            updateList(it)
        }
        viewModel.queryList.observe(viewLifecycleOwner) {
            updateList(it)
        }
    }
    private fun updateList(userList : List<UserModel>){
        adapter.userList = userList
        binding.searchRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}