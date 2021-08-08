package com.smitcoderx.learn.trippin_business.UI.Fragments.Login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.style.FoldingCube
import com.google.android.material.snackbar.Snackbar
import com.smitcoderx.learn.trippin_business.API.ApiClient
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.Util.Constants
import com.smitcoderx.learn.trippin_business.Util.PreferenceManager
import com.smitcoderx.learn.trippin_business.databinding.FragmentLoginBinding
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.fabLogin.setOnClickListener {
            if (checkValidations()) {
                binding.transparentLayout.visibility = View.VISIBLE
                binding.progress.visibility = View.VISIBLE
                binding.spinKit.visibility = View.VISIBLE
                login()
            } else {
                Snackbar.make(requireView(), "Please Fill all the Fields", Snackbar.LENGTH_SHORT)
                    .show()
            }

        }

        binding.fabSignup.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToLoginSignupFragment())
        }
    }

    private fun login() {
        val prefs = PreferenceManager(requireContext())
        val username = binding.tiLoginUsername.editText!!.text.toString().trim()
            .lowercase(Locale.getDefault())
        val password = binding.tiLoginPassword.editText!!.text.toString().trim()
        val progressBar = binding.progress
        val foldingCube = FoldingCube()
        progressBar.indeterminateDrawable = foldingCube

        lifecycleScope.launchWhenCreated {
            val response = try {
                ApiClient.retrofitService.login(username, password)
            } catch (e: IOException) {
                Log.e(Constants.TAG, "signUp IOExecption: ${e.message}")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(Constants.TAG, "signUp HttpException: ${e.message}")
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                binding.transparentLayout.visibility = View.GONE
                progressBar.visibility = View.GONE
                binding.spinKit.visibility = View.GONE
                val login = response.body()
                if (login!!.message == "username or password does not match") {
                    Snackbar.make(
                        requireView(),
                        login.message.uppercase(Locale.getDefault()), Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    prefs.setToken(login.token)
                    prefs.setLoggedIn(true)
                    Snackbar.make(
                        requireView(),
                        login.message.uppercase(Locale.getDefault()), Snackbar.LENGTH_LONG
                    ).show()
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                }
            }
        }
    }

    private fun checkValidations(): Boolean {
        if (binding.tiLoginUsername.editText!!.text.isNullOrEmpty()) {
            binding.tiLoginUsername.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiLoginPassword.editText!!.text.isNullOrEmpty()) {
            binding.tiLoginPassword.error = resources.getString(R.string.field_required)
            return false
        }
        return true
    }

}