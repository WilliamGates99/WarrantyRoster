package com.xeniac.warrantyroster_manager.onboarding.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingAdapter(
    private val fragmentsList: ArrayList<Fragment>,
    fragmentManager: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifeCycle) {

    override fun getItemCount(): Int = fragmentsList.size

    override fun createFragment(position: Int): Fragment = fragmentsList[position]
}