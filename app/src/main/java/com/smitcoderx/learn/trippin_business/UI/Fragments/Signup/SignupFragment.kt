package com.smitcoderx.learn.trippin_business.UI.Fragments.Signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.databinding.FragmentSignupBinding

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)

        binding.fabLogin.setOnClickListener {
            findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginSignupFragment())
        }

        binding.fabSignup.setOnClickListener {
            findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }
    }

}