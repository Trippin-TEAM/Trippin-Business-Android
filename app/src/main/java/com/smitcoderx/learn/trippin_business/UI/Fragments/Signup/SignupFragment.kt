package com.smitcoderx.learn.trippin_business.UI.Fragments.Signup

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.ybq.android.spinkit.style.CubeGrid
import com.google.android.material.snackbar.Snackbar
import com.smitcoderx.learn.trippin_business.API.ApiClient
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.UI.Dialogs.BottomTypeFragment
import com.smitcoderx.learn.trippin_business.UI.Dialogs.BottomTypeFragment.PassData
import com.smitcoderx.learn.trippin_business.Util.Constants.TAG
import com.smitcoderx.learn.trippin_business.databinding.FragmentSignupBinding
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class SignupFragment : Fragment(R.layout.fragment_signup), PassData {

    private lateinit var binding: FragmentSignupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)

        binding.fabLogin.setOnClickListener {
            findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginSignupFragment())
        }

        binding.fabSignup.setOnClickListener {
            if (checkValidations()) {
                binding.transparentLayout.visibility = View.VISIBLE
                binding.progress.visibility = View.VISIBLE
                binding.spinKit.visibility = View.VISIBLE
                signUp()
            } else {
                Snackbar.make(requireView(), "Please Fill all the Fields", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.tiType.editText!!.setOnClickListener {
            val filter = BottomTypeFragment()
            filter.setTargetFragment(this, 1)
            filter.show(parentFragmentManager, "Dialog")
        }
    }

    override fun onPassType(type: String) {
        binding.tiType.editText!!.setText(type)
    }

    private fun checkValidations(): Boolean {
        if (binding.tiBusinessname.editText!!.text.isNullOrEmpty()) {
            binding.tiBusinessname.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiSignupEmail.editText!!.text.isNullOrEmpty()) {
            binding.tiSignupEmail.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiUsername.editText!!.text.isNullOrEmpty()) {
            binding.tiUsername.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiMobileno.editText!!.text.isNullOrEmpty()) {
            binding.tiMobileno.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiSignupPassword.editText!!.text.isNullOrEmpty()) {
            binding.tiSignupPassword.error = resources.getString(R.string.field_required)
            return false
        } else if (binding.tiSignupPassword.editText!!.text.length < 8) {
            binding.tiSignupPassword.error = resources.getString(R.string.password_limit)
            return false
        }
        if (binding.tiAddress.editText!!.text.isNullOrEmpty()) {
            binding.tiAddress.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiCity.editText!!.text.isNullOrEmpty()) {
            binding.tiCity.error = resources.getString(R.string.field_required)
            return false
        }
        if (binding.tiType.editText!!.text.isNullOrEmpty()) {
            binding.tiType.error = resources.getString(R.string.field_required)
            return false
        }
        return true
    }

    private fun signUp() {

        val username = binding.tiUsername.editText!!.text.toString().trim()
            .lowercase(Locale.getDefault())
        val password = binding.tiSignupPassword.editText!!.text.toString().trim()
        val name = binding.tiBusinessname.editText!!.text.toString().trim()
        val email = binding.tiSignupEmail.editText!!.text.toString().trim()
        val mobileNo = binding.tiMobileno.editText!!.text.toString().trim()
        val address = binding.tiAddress.editText!!.text.toString().trim()
        val city = binding.tiCity.editText!!.text.toString().trim()
        val type = binding.tiType.editText!!.text.toString().trim()
        val progressBar = binding.progress
        val cubeGrid = CubeGrid()
        progressBar.indeterminateDrawable = cubeGrid

        lifecycleScope.launchWhenCreated {
            val response = try {
                ApiClient.retrofitService.register(
                    username,
                    password,
                    name,
                    email,
                    mobileNo,
                    address,
                    "",
                    city,
                    type
                )
            } catch (e: IOException) {
                Log.e(TAG, "signUp IOExecption: ${e.message}")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "signUp HttpException: ${e.message}")
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                binding.transparentLayout.visibility = View.GONE
                progressBar.visibility = View.GONE
                binding.spinKit.visibility = View.GONE
                val register = response.body()
                if (register!!.message == "user already exists") {
                    Snackbar.make(
                        requireView(),
                        register.message.uppercase(Locale.getDefault()), Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        requireView(),
                        register.message.uppercase(Locale.getDefault()), Snackbar.LENGTH_LONG
                    ).show()
                    findNavController().navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
                }
            }
        }
    }

}