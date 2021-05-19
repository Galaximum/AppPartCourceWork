package ru.hse.project.ecoapp.util

import android.app.Activity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.hse.project.ecoapp.R

class ViewUtils {
    companion object{
        fun bottomNavigationHidden(activity:Activity, duration: Long) {
            activity.findViewById<BottomNavigationView>(R.id.bottom_Nav_View)
                .apply {
                    this.animate().translationY(this.height.toFloat()).duration = duration
                }
        }

        fun bottomNavigationShowed(activity:Activity, duration: Long) {
            activity.findViewById<BottomNavigationView>(R.id.bottom_Nav_View).apply {
                this.animate().translationY(0F).duration = duration
            }
        }

        fun updateAnchor(anchorView: View, newAnchor:Int){
            val params = anchorView.layoutParams as CoordinatorLayout.LayoutParams
            params.anchorId = R.id.fragment_map_bottom_sheet_search
            anchorView.layoutParams = params
        }

    }

}