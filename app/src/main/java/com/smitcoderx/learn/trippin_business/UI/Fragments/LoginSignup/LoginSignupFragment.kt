package com.smitcoderx.learn.trippin_business.UI.Fragments.LoginSignup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.databinding.FragmentLoginSignupBinding

class LoginSignupFragment : Fragment(R.layout.fragment_login_signup) {

    private lateinit var binding: FragmentLoginSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginSignupBinding.bind(view)

        binding.btnSignup.setOnClickListener {
            findNavController().navigate(LoginSignupFragmentDirections.actionLoginSignupFragmentToSignupFragment())
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(LoginSignupFragmentDirections.actionLoginSignupFragmentToLoginFragment())
        }
    }

}