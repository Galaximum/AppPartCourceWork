package ru.hse.project.ecoapp.ui.main.profile.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.AuthActivity
import ru.hse.project.ecoapp.BackPressedForFragments
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.ui.main.profile.setting.email.DialogFragmentEditEmail
import ru.hse.project.ecoapp.ui.main.profile.setting.nickname.DialogFragmentEditNickname
import ru.hse.project.ecoapp.ui.main.profile.setting.password.DialogFragmentEditPassword

class SettingsFragment : Fragment(), BackPressedForFragments {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = App.getComponent().getRepository().user!!
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val tool: Toolbar = root.findViewById(R.id.toolbar3)
        val btnChangeNickname: ConstraintLayout =
            root.findViewById(R.id.settings_frag_btn_change_nickname)
        val btnChangeEmail: ConstraintLayout =
            root.findViewById(R.id.settings_frag_btn_change_email)
        val btnChangePassword: ConstraintLayout =
            root.findViewById(R.id.settings_frag_btn_change_password)
        val btnSignOut: ConstraintLayout = root.findViewById(R.id.settings_frag_btn_sign_out)
        val btnDeleteUser: ConstraintLayout = root.findViewById(R.id.settings_frag_btn_delete_user)


        tool.setNavigationIcon(R.drawable.arrow_back_v2)

        tool.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        tool.title = requireContext().resources.getString(R.string.title_settings)
        tool.setTitleTextColor(requireContext().resources.getColor(R.color.white))



        btnChangeNickname.setOnClickListener {
            DialogFragmentEditNickname().show(parentFragmentManager, "EDIT_NICK_NAME")
        }

        btnChangeEmail.setOnClickListener {
            DialogFragmentEditEmail().show(parentFragmentManager, "EDIT_EMAIL")
        }

        btnChangePassword.setOnClickListener {
            DialogFragmentEditPassword().show(parentFragmentManager, "EDIT_PASSWORD")
        }

        btnSignOut.setOnClickListener {
            user.signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            if (activity != null) {
                activity?.finishAfterTransition()
            }
        }

        btnDeleteUser.setOnClickListener {
            user.deleteUser().addOnSuccessListener {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                if (activity != null) {
                    activity?.finishAfterTransition()
                }
            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }


        return root
    }

    override fun onBackPressed(): Boolean {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_Nav_View)
            .animate().translationY(0F).duration = 150
        App.getComponent()
            .setActiveFragment(parentFragmentManager.findFragmentByTag("PROFILE_FRAG")!!)
        return true
    }
}