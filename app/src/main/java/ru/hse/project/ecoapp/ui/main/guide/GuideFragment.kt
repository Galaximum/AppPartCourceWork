package ru.hse.project.ecoapp.ui.main.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.BackPressedForFragments
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.ui.main.guide.recycler.GuideAdapter

class GuideFragment : Fragment(), BackPressedForFragments {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var recycler: RecyclerView
    private lateinit var containerI: FrameLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_guide, container, false)
        recycler = root.findViewById(R.id.recycler_guides)
        bottomNav = requireActivity().findViewById(R.id.bottom_Nav_View)
        containerI = requireActivity().findViewById<FrameLayout>(R.id.main_container)


        val adapter = GuideAdapter(
            arrayListOf(
                R.string.type_paper,
                R.string.type_glass,
                R.string.type_plastic,
                R.string.type_metal
            ),
            arrayListOf(
                R.drawable.paper_white,
                R.drawable.glass_white,
                R.drawable.plastic_white,
                R.drawable.metal_white
            ),
            arrayListOf(
                R.color.color_type_marker_paper,
                R.color.color_type_marker_glass,
                R.color.color_type_marker_plastic,
                R.color.color_type_marker_metal
            )
        )

        val p = recycler.layoutParams as ConstraintLayout.LayoutParams
        p.bottomMargin = 154
        recycler.layoutParams = p

        recycler.adapter = adapter
        adapter.setHolderClick{it:Int->
            val defGuide = DefaultGuideFragment()
            defGuide.arguments = Bundle().apply { this.putBoolean(it.toString(), true) }
            parentFragmentManager.beginTransaction()
                .add(R.id.main_container,defGuide, "GUIDE_DEFAULT_FRAG")
                .setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                .hide(App.getComponent().getActiveFragment()!!)
                .addToBackStack("GUIDE_LIST_TO_GUIDE").commit()
            App.getComponent().setActiveFragment(defGuide)
        }


        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, p1: Int) {
            }

            fun onScroll(
                p0: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {

                val endHasBeenReached = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {

                }
            }

        })



        return root
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