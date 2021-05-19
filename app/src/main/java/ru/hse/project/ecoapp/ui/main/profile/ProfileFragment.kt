package ru.hse.project.ecoapp.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.BackPressedForFragments
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.ui.main.profile.rating.DialogFragmentShowRating
import ru.hse.project.ecoapp.ui.main.profile.setting.SettingsFragment

class ProfileFragment : Fragment(), BackPressedForFragments {
    private val repository = App.getComponent().getRepository()
    private val user = repository.user!!
    private val picasso = App.getComponent().getPicassoHttps()
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        bottomNav = requireActivity().findViewById(R.id.bottom_Nav_View)
        val nickName: TextView = root.findViewById(R.id.profile_frag_nickname)
        val userName: TextView = root.findViewById(R.id.profile_frag_username)
         imageView = root.findViewById(R.id.profile_frag_image)
        val btnEdit: ConstraintLayout = root.findViewById(R.id.profile_frag_btn_open_settings)
        val btnRating: ConstraintLayout = root.findViewById(R.id.profile_frag_btn_open_rating)

        nickName.text = user.nickName
        userName.text = "${user.secondName}  ${user.firstName}"


        val updateImage = { updateImageView() }.apply { invoke() }
        imageView.setOnClickListener {
            DialogFragmentChoseEventSetImage(updateImage).show(parentFragmentManager, "TAG")
        }
        btnEdit.setOnClickListener {
            val frag = SettingsFragment()
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .hide(parentFragmentManager.findFragmentByTag("PROFILE_FRAG")!!)
                .add(R.id.main_container, frag, "SETTINGS_FRAGMENT")
                .addToBackStack("PROFILE_TO_SETTINGS")
                .commit()
            App.getComponent().setActiveFragment(frag)

            bottomNav.apply {
                this.animate().translationY(1F * this.height).duration = 150
            }
        }

        btnRating.setOnClickListener {
            //Show dialog fragment
            DialogFragmentShowRating().show(parentFragmentManager, "RATING")
        }



        return root
    }

    private fun updateImageView() {
        val url = repository.user!!.urlImage
        picasso.load(url)
            .fit()
            .centerCrop()
            .error(R.drawable.place_holder_user)
            .into(imageView)

    }

    override fun onBackPressed(): Boolean {

        parentFragmentManager.beginTransaction()
            .hide(App.getComponent().getActiveFragment()!!)
            .show(parentFragmentManager.findFragmentByTag("MAP_FRAG")!!)
            .commit()
        App.getComponent().setActiveFragment(parentFragmentManager.findFragmentByTag("MAP_FRAG")!!)

        bottomNav.menu[1].isChecked = true
        return false
    }

}