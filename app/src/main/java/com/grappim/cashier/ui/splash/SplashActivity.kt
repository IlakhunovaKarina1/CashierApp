package com.grappim.cashier.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.grappim.cashier.R
import com.grappim.cashier.ui.root.MainActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            MainActivity.start(this@SplashActivity)

            finish()
        }
    }
}