package com.halilmasali.todoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.halilmasali.todoapp.R
import com.halilmasali.todoapp.ui.fragments.CustomFragmentManager
import com.halilmasali.todoapp.ui.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    lateinit var customFragmentManager: CustomFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customFragmentManager =
            CustomFragmentManager(supportFragmentManager, R.id.fragmentContainerView)
        customFragmentManager.replaceFragment(MainFragment())
    }
}