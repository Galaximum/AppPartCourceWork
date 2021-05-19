package ru.hse.project.ecoapp.ui.auth.emailAuth

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.hse.project.ecoapp.R

class DialogFragmentEmailAuth : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_email_auth, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val viewPager = view.findViewById<ViewPager2>(R.id.email_view_pager)
        viewPager.adapter = EmailAuthPageAdapter(requireActivity().supportFragmentManager, lifecycle)
        val tabLayout = view.findViewById<TabLayout>(R.id.email_tab_layout)

        val titles = arrayOf<Int>(R.string.btn_registration,R.string.btn_sign_in)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text =requireContext().resources.getString(titles[position])
        }.attach()
        return view;
    }


}