package com.smitcoderx.learn.trippin_business.UI.Fragments.Home

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.smitcoderx.learn.trippin_business.API.ApiClient
import com.smitcoderx.learn.trippin_business.Adapters.ReviewAdapter
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.UI.MainActivity
import com.smitcoderx.learn.trippin_business.Util.Constants.IMAGE_URL
import com.smitcoderx.learn.trippin_business.Util.Constants.TAG
import com.smitcoderx.learn.trippin_business.Util.PreferenceManager
import com.smitcoderx.learn.trippin_business.databinding.FragmentHomeBinding
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val prefs = PreferenceManager(requireContext())
        val token = prefs.getToken()

        getMe(token!!)

        setupRv()

        binding.ivBusiness.setOnClickListener {
            prefs.logoutUser()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }

    }

    private fun toolbar(placeName: String) {
        binding.toolbar.apply {
            (activity as MainActivity).setSupportActionBar(this)

            var isShow = true
            var scrollRange = -1
            binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                if (scrollRange == -1) {
                    scrollRange = barLayout?.totalScrollRange!!
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbarLayout.title = placeName
                    binding.collapsingToolbarLayout.setCollapsedTitleTextColor(resources.getColor(R.color.white))
                    isShow = true
                } else if (isShow) {
                    binding.collapsingToolbarLayout.title =
                        " " //careful there should a space between double quote otherwise it wont work
                    isShow = false
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getMe(token: String) {
        lifecycleScope.launchWhenCreated {
            val response = try {
                ApiClient.retrofitService.getMe(token)
            } catch (e: IOException) {
                Log.e(TAG, "getMe IO: ${e.message}")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "getMe Http: ${e.message}")
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()
                val id = user!!._id
                val placeName = user.name
                val imageUrl = IMAGE_URL + user._id + ".jpg"

                binding.apply {
                    tvPlaceName.text = placeName
                    tvPlaceName.setOnClickListener { }
                    tvUsername.text = "@${user.username}"
                    Glide.with(requireContext())
                        .load(user.image)
                        .centerCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                activity?.let {
                                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUploadImageActivity())
                                }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(ivBusiness)
                }
                getReviews(id)
                toolbar(placeName)
            }
        }
    }

    private fun getReviews(id: String) {
        lifecycleScope.launchWhenCreated {
            val response = try {
                ApiClient.retrofitService.getReviews(id)
            } catch (e: IOException) {
                Log.e(TAG, "getReview IO: ${e.message}")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "getReview Http: ${e.message}")
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                val reviews = response.body()
                reviewAdapter.differ.submitList(reviews)
            } else {
                binding.rvReview.visibility = View.GONE
                Snackbar.make(
                    binding.homeLayout,
                    "No Reviews Available for your Place",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupRv() {
        reviewAdapter = ReviewAdapter()
        binding.rvReview.apply {
            setHasFixedSize(true)
            adapter = reviewAdapter
        }
    }
}