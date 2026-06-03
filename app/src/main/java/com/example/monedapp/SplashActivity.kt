package com.example.monedapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.monedapp.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar y aplicar la animación personalizada
        val splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        
        binding.ivLogo.startAnimation(splashAnim)
        binding.tvAppName.startAnimation(splashAnim)
        binding.tvAuthor.startAnimation(splashAnim)
        binding.tvGroup.startAnimation(splashAnim)
        binding.progressBar.startAnimation(splashAnim)

        // Navegar a MainActivity después de 3.5 segundos para dar tiempo a la animación
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3500)
    }
}
