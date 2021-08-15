package com.smitcoderx.learn.trippin_business.UI.Fragments.Splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smitcoderx.learn.trippin_business.R
import com.smitcoderx.learn.trippin_business.Util.Constants.SPLASH_DELAY
import com.smitcoderx.learn.trippin_business.Util.PreferenceManager
import com.smitcoderx.learn.trippin_business.databinding.FragmentSplashBinding
import kotlinx.coroutines.*

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding: FragmentSplashBinding
    private val splashScope = CoroutineScope(Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)

        val prefs = PreferenceManager(requireContext())

        if (prefs.getLoggedIn()) {
            splashScope.launch {
                delay(SPLASH_DELAY)
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
            }
        } else {
            splashScope.launch {
                delay(SPLASH_DELAY)
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLandingFragment())
            }
        }
    }

    override fun onPause() {
        splashScope.cancel()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        splashScope.cancel()
    }
}