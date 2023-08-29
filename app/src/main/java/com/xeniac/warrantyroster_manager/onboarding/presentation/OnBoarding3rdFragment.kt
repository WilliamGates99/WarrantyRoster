package com.xeniac.warrantyroster_manager.onboarding.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentOnboarding3rdBinding
import com.xeniac.warrantyroster_manager.util.Constants.ONBOARDING_2ND_INDEX
import com.xeniac.warrantyroster_manager.util.Constants.ONBOARDING_4TH_INDEX

class OnBoarding3rdFragment : Fragment(R.layout.fragment_onboarding_3rd) {

    private var _binding: FragmentOnboarding3rdBinding? = null
    val binding get() = _binding!!

    private var viewPager: ViewPager2? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboarding3rdBinding.bind(view)

        viewPager = requireActivity().findViewById(R.id.viewpager)

        backOnClick()
        nextOnClick()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun backOnClick() = binding.btnOnboarding3rdBack.setOnClickListener {
        // ViewPager items start from 0
        viewPager?.currentItem = ONBOARDING_2ND_INDEX
    }

    private fun nextOnClick() = binding.btnOnboarding3rdNext.setOnClickListener {
        // ViewPager items start from 0
        viewPager?.currentItem = ONBOARDING_4TH_INDEX
    }
}