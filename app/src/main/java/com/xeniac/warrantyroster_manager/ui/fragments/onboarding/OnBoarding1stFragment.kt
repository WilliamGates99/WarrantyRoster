package com.xeniac.warrantyroster_manager.ui.fragments.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.xeniac.warrantyroster_manager.R
import com.xeniac.warrantyroster_manager.databinding.FragmentOnboarding1stBinding
import com.xeniac.warrantyroster_manager.utils.Constants.ONBOARDING_2ND_INDEX
import com.xeniac.warrantyroster_manager.utils.Constants.ONBOARDING_4TG_INDEX

class OnBoarding1stFragment : Fragment(R.layout.fragment_onboarding_1st) {

    private var _binding: FragmentOnboarding1stBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboarding1stBinding.bind(view)

        viewPager = requireActivity().findViewById(R.id.viewpager)

        skipOnClick()
        nextOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun skipOnClick() = binding.btnSkip.setOnClickListener {
        // ViewPager items start from 0
        viewPager.currentItem = ONBOARDING_4TG_INDEX
    }

    private fun nextOnClick() = binding.btnNext.setOnClickListener {
        // ViewPager items start from 0
        viewPager.currentItem = ONBOARDING_2ND_INDEX
    }
}