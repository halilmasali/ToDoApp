package com.halilmasali.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    lateinit var customFragmentManager: CustomFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customFragmentManager =
            CustomFragmentManager(supportFragmentManager, R.id.fragmentContainerView)
        customFragmentManager.replaceFragment(MainFragment())
    }
}