package ru.hse.project.ecoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.project.ecoapp.ui.main.guide.GuideFragment
import ru.hse.project.ecoapp.ui.main.map.MapFragment
import ru.hse.project.ecoapp.ui.main.profile.ProfileFragment


class MainActivity : AppCompatActivity() {


    private val fm: FragmentManager = supportFragmentManager
    private lateinit var guideFragment: Fragment
    private lateinit var mapFragment: Fragment
    private lateinit var profileFragment: Fragment
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottom_Nav_View)
        bottomNav.menu[1].isChecked = true

        guideFragment = GuideFragment()
        mapFragment = MapFragment()
        profileFragment = ProfileFragment()
        App.getComponent().setActiveFragment(mapFragment)

        fm.beginTransaction().apply {
            add(R.id.main_container, guideFragment, "GUIDE_FRAG").hide(guideFragment)
            add(R.id.main_container, profileFragment, "PROFILE_FRAG").hide(profileFragment)
            add(R.id.main_container, mapFragment, "MAP_FRAG")
        }.commit()


        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.guideFragment -> {
                    if (App.getComponent().getActiveFragment() == guideFragment) {
                        return@setOnNavigationItemSelectedListener false
                    }

                    fm.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .hide(App.getComponent().getActiveFragment()!!)
                        .show(guideFragment)
                        .commit()


                    App.getComponent().setActiveFragment(guideFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.mapFragment -> {
                    if (App.getComponent().getActiveFragment() == mapFragment) {
                        return@setOnNavigationItemSelectedListener false
                    }
                    val transaction = fm.beginTransaction()
                    when (App.getComponent().getActiveFragment()!!) {
                        guideFragment -> {
                            transaction.hide(guideFragment)
                        }
                        profileFragment -> {
                            transaction.hide(profileFragment)
                        }
                    }

                    transaction.show(mapFragment).commit()
                    App.getComponent().setActiveFragment(mapFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profileFragment -> {
                    if (App.getComponent().getActiveFragment() == profileFragment) {
                        return@setOnNavigationItemSelectedListener false
                    }
                    fm.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                        )
                        .hide(App.getComponent().getActiveFragment()!!)
                        .show(profileFragment)
                        .commit()

                    App.getComponent().setActiveFragment(profileFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }


    }


    private fun checkPermission(perm: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            perm
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onStart() {
        super.onStart()

        val permissions = arrayListOf<String>(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        for (i in permissions.lastIndex downTo 0) {
            if (checkPermission(permissions[i])) {
                permissions.remove(permissions[i])
            }
        }

        val arr = permissions.toArray(arrayOf<String>())
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, arr, 0)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        if (App.getComponent().getRepository().user != null) {
            App.getComponent().getRepository().updateMemory()
        }
        super.onStop()
    }

    override fun onBackPressed() {
        if ((App.getComponent().getActiveFragment() as BackPressedForFragments).onBackPressed()) {
            super.onBackPressed()
        }
    }
}