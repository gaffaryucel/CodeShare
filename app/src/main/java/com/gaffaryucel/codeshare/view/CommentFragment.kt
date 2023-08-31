package com.gaffaryucel.codeshare.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gaffaryucel.codeshare.R
import com.gaffaryucel.codeshare.adapter.CommentAdapter
import com.gaffaryucel.codeshare.adapter.HomePageAdapter
import com.gaffaryucel.codeshare.adapter.StoryAdapter
import com.gaffaryucel.codeshare.databinding.FragmentChangeProfileInfosBinding
import com.gaffaryucel.codeshare.databinding.FragmentCommentBinding
import com.gaffaryucel.codeshare.model.CommentModel
import com.gaffaryucel.codeshare.util.Status
import com.gaffaryucel.codeshare.viewmodel.ChangeProfileInfosViewModel
import com.gaffaryucel.codeshare.viewmodel.CommentViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class CommentFragment : Fragment() {
    private lateinit var viewModel: CommentViewModel
    private lateinit var binding : FragmentCommentBinding

    private lateinit var adapter: CommentAdapter

    private val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

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

        binding = FragmentCommentBinding.inflate(inflater,container,false)

        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommentAdapter()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
        val postId = arguments?.getString("post") ?: ""
        val postOwnerId = arguments?.getString("user") ?: ""
        viewModel.getUserProfileInfo(postOwnerId)
        viewModel.getPostInfo(postOwnerId,postId)
        viewModel.getComments(postOwnerId,postId)
        observeLiveData()

        binding.shareCommentButton.setOnClickListener {
            val comment = generateComment(postOwnerId,postId)
            viewModel.createComment(comment)
        }
    }
    private fun generateComment(postOwnerId : String,postId : String) : CommentModel{
        val userComment = binding.newCommentEditText.text.toString()
        val comment = CommentModel()
        comment.commentText = userComment
        comment.commentOwner = currentUser
        comment.commentId = UUID.randomUUID().toString()
        comment.postOwner = postOwnerId
        comment.postId = postId
        comment.time = viewModel.getCurrentDateTime()
        binding.newCommentEditText.setText("")
        return comment
    }
    private fun observeLiveData(){
        viewModel.post.observe(viewLifecycleOwner, Observer { post->
            when(post.status){
                Status.SUCCESS->{
                    Glide.with(requireContext()).load(post.data!!.content).into(binding.postIvInComments)
                }
                Status.ERROR->{
                    Toast.makeText(requireContext(), "Upload Faild", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                    Toast.makeText(requireContext(), "Uploading", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer { user->
            when(user.status){
                Status.SUCCESS->{
                    Glide.with(requireContext()).load(user.data!!.profileImageUrl).into(binding.userProfilePhotoInComments)
                    binding.userNameInCommentsFragment.text = user.data.name
                }
                Status.ERROR->{
                    Toast.makeText(requireContext(), "Upload Faild", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING->{
                    Toast.makeText(requireContext(), "Uploading", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.comments.observe(viewLifecycleOwner) {
            if (it.data != null){
                val list  = it.data
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

                // Tarihleri sıralayın ve en yeni tarihleri en üstte tutacak şekilde alın
                val sortedList = list.sortedByDescending { comment ->
                    try {
                        dateFormat.parse(comment.time.toString())?.time ?: 0
                    } catch (e: Exception) {
                        0
                    }
                }
                adapter.commentList = sortedList
                binding.commentsRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
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