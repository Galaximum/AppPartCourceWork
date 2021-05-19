package ru.hse.project.ecoapp.ui.auth.emailAuth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hse.project.ecoapp.ui.auth.emailAuth.login.EmailLoginFragment
import ru.hse.project.ecoapp.ui.auth.emailAuth.registration.EmailRegistrationFragment

class EmailAuthPageAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {


    companion object {
        const val COUNT_PAGE = 2
    }

    override fun getItemCount(): Int {
        return COUNT_PAGE
    }

    override fun createFragment(position: Int): Fragment {

        var fragment: Fragment = Fragment()
        when (position) {
            0 -> fragment = EmailRegistrationFragment()
            1 -> fragment = EmailLoginFragment()
        }
        return fragment;
    }

}