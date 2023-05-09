package com.xeniac.warrantyroster_manager.core.presentation.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    @Inject
    lateinit var fragmentFactory: LandingFragmentFactory

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}