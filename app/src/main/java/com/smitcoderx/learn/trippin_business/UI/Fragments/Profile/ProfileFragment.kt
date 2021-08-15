package com.smitcoderx.learn.trippin_business.UI.Fragments.Profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.AppBarLayout
import com.smitcoderx.learn.trippin_business.API.ApiClient
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.UI.MainActivity
import com.smitcoderx.learn.trippin_business.Util.Constants.TAG
import com.smitcoderx.learn.trippin_business.Util.PreferenceManager
import com.smitcoderx.learn.trippin_business.databinding.FragmentProfileBinding
import retrofit2.HttpException
import java.io.IOException

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        toolbar()

        val prefs = PreferenceManager(requireContext())
        val token = prefs.getToken()

        getMe(token!!)

        binding.fabEdit.setOnClickListener {
            allEnabled()
        }

        binding.fabSave.setOnClickListener {
            allDisabled()
        }

        binding.fabCamera.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUploadImageActivity())
        }

    }

    private fun toolbar() {
        binding.toolbar.apply {
            (activity as MainActivity).setSupportActionBar(this)

            var isShow = true
            var scrollRange = -1
            binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                if (scrollRange == -1) {
                    scrollRange = barLayout?.totalScrollRange!!
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbarLayout.title = resources.getString(R.string.profile)
                    binding.collapsingToolbarLayout.setCollapsedTitleTextColor(resources.getColor(R.color.white))
                    binding.fabCamera.hide()
                    isShow = true
                } else if (isShow) {
                    binding.collapsingToolbarLayout.title =
                        " " //careful there should a space between double quote otherwise it wont work
                    binding.fabCamera.show()
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

                binding.apply {
                    /*                  tvPlaceName.text = user!!.name
                                      tvPlaceName.setOnClickListener { }
                                      tvUsername.text = "@${user.username}"*/
                    Glide.with(requireContext())
                        .load(user!!.image)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(ivMe)
                }
            }
        }
    }

    private fun allEnabled() {
        binding.apply {
            fabSave.show()
            fabEdit.hide()
        }
    }

    private fun allDisabled() {
        binding.apply {
            fabEdit.show()
            fabSave.hide()
        }
    }

}