package com.xeniac.warrantyroster_manager.core.presentation.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xeniac.warrantyroster_manager.databinding.ActivityLandingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}