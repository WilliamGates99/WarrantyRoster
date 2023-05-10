package com.xeniac.warrantyroster_manager.core.presentation.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import com.xeniac.warrantyroster_manager.di.FragmentFactoryEntryPoint
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setCustomFragmentFactory()
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setCustomFragmentFactory() {
        val entryPoint = EntryPointAccessors.fromActivity(
            activity = this,
            entryPoint = FragmentFactoryEntryPoint::class.java
        )
        supportFragmentManager.fragmentFactory = entryPoint.getLandingFragmentFactory()
    }
}