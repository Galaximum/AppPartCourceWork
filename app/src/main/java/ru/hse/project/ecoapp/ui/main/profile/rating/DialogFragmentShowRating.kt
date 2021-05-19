package ru.hse.project.ecoapp.ui.main.profile.rating

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.R

class DialogFragmentShowRating : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val user = App.getComponent().getRepository().user!!
        val root = inflater.inflate(R.layout.dialog_fragment_show_rating, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val recycler: RecyclerView = root.findViewById(R.id.recycler_show_rating)
        val score: TextView = root.findViewById(R.id.fragment_rating_score)
        val position: TextView = root.findViewById(R.id.fragment_rating_position)

        App.getComponent().getRepository().getRatingUsers(0, 100).addOnSuccessListener {
            recycler.adapter = RatingAdapter(it.sortedBy { x -> x.position })
        }

        if(user.myRating!=null) {
            score.text = "  ${user.myRating!!.score}"
            position.text = "${position.text} ${user.myRating!!.position + 1}"
        }else{
            score.text = " -"
            position.text = "-"
        }

        return root
    }
}