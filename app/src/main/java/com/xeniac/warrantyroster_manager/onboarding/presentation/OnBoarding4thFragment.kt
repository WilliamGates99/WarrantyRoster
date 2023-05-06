package com.xeniac.warrantyroster_manager.onboarding.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentOnboarding4thBinding
import com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding.OnBoardingFragmentDirections

class OnBoarding4thFragment : Fragment(R.layout.fragment_onboarding_4th) {

    private var _binding: FragmentOnboarding4thBinding? = null
    val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboarding4thBinding.bind(view)

        startOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startOnClick() = binding.btnOnboarding4thStart.setOnClickListener {
        navigateToLoginFragment()
    }

    private fun navigateToLoginFragment() = findNavController().navigate(
        OnBoardingFragmentDirections.actionOnBoardingFragmentToAuthFragment()
    )
}