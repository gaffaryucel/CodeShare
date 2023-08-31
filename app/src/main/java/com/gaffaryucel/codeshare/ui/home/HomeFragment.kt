package com.gaffaryucel.codeshare.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gaffaryucel.codeshare.adapter.HomePageAdapter
import com.gaffaryucel.codeshare.adapter.StoryAdapter
import com.gaffaryucel.codeshare.databinding.FragmentHomeBinding
import com.gaffaryucel.codeshare.model.PostModel
import com.gaffaryucel.codeshare.model.Stories
import com.gaffaryucel.codeshare.model.UserModel
import com.gaffaryucel.codeshare.view.ShareActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var adapter: HomePageAdapter
    private lateinit var storyAdapter : StoryAdapter

    private var userList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HomePageAdapter()

        binding.storiesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        storyAdapter = StoryAdapter()


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getPosts()
            viewModel.getStories(userList)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.cameraIcon.setOnClickListener{
            val intent = Intent(requireContext(),ShareActivity::class.java)
            intent.putExtra("story","story")
            requireActivity().startActivity(intent)
        }

        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.posts.observe(viewLifecycleOwner) {
            if (it != null){
                val list = it
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                // Tarihleri sıralayın ve en yeni tarihleri en üstte tutacak şekilde alın
                val sortedList = list.sortedByDescending { post ->
                    try {
                        dateFormat.parse(post.postId.toString())?.time ?: 0
                    } catch (e: Exception) {
                        0
                    }
                }
                adapter.postList = sortedList
                binding.postRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

        }
        viewModel.stories.observe(viewLifecycleOwner) {
            storyAdapter.storiesList = it
            binding.storiesRecyclerView.adapter = storyAdapter
            storyAdapter.notifyDataSetChanged()
        }
        viewModel.users.observe(viewLifecycleOwner) {
            viewModel.getStories(it)
            userList = it as ArrayList<String>
        }
    }

    override fun onResume() {
        super.onResume()
        println("resume")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}