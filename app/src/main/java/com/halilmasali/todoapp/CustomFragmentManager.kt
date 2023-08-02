package com.halilmasali.todoapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class CustomFragmentManager(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun addFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(containerId, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}