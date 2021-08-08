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
import com.smitcoderx.learn.trippin_business.API.ApiClient
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.Util.Constants.IMAGE_URL
import com.smitcoderx.learn.trippin_business.Util.Constants.TAG
import com.smitcoderx.learn.trippin_business.Util.PreferenceManager
import com.smitcoderx.learn.trippin_business.databinding.FragmentHomeBinding
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val prefs = PreferenceManager(requireContext())
        val token = prefs.getToken()

        getMe(token!!)

        binding.ivBusiness.setOnClickListener {
            prefs.logoutUser()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
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
                val imageUrl = IMAGE_URL + user!!._id + ".jpg"

                binding.apply {
                    tvPlaceName.text = user.name
                    tvPlaceName.setOnClickListener { }
                    tvUsername.text = "@${user.username}"
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .centerCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToImageUploadFragment())
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
            }
        }
    }
}