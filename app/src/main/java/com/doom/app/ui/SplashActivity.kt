package com.doom.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.doom.app.databinding.ActivitySplashBinding
import com.doom.app.ui.home.HomeActivity
import com.doom.app.util.transparentStatusBar

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        transparentStatusBar(true)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateNextScreen()
        animateText("Pedro Pedro...")
    }

    private fun animateText(text: String) {
        binding.splashText.text = ""
        var index = 0
        val handler = Handler(Looper.getMainLooper())
        val typingSpeed: Long = 100

        val runnable = object : Runnable {
            override fun run() {
                if (index < text.length) {
                    binding.splashText.text = binding.splashText.text.toString() + text[index]
                    index++
                    handler.postDelayed(this, typingSpeed)
                }
            }
        }
        handler.post(runnable)
    }

    private fun navigateNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 3000)
    }
}
