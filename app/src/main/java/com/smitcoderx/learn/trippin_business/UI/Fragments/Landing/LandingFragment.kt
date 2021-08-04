package com.smitcoderx.learn.trippin_business.UI.Fragments.Landing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.databinding.FragmentLandingBinding

class LandingFragment : Fragment(R.layout.fragment_landing) {

    private lateinit var binding: FragmentLandingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLandingBinding.bind(view)

        binding.fab.setOnClickListener {
            findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToLoginSignupFragment())
        }

    }

}