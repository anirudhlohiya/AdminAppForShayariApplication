package com.example.adminappformyshayaiapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        val handler = Handler()
        val runnable = Runnable {
            startActivity(Intent(this, MainActivity()::class.java))
            finish()
        }
        handler.postDelayed(runnable, 1000)
    }
}