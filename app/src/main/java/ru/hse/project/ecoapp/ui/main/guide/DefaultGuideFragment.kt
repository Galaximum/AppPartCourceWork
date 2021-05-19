package ru.hse.project.ecoapp.ui.main.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.BackPressedForFragments
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.TrashTypes
import ru.hse.project.ecoapp.util.ViewUtils


class DefaultGuideFragment : Fragment(), BackPressedForFragments {
    private lateinit var tool: Toolbar
    private lateinit var textWhatIsTrash: TextView
    private lateinit var textWhatIsNotTrash: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        ViewUtils.bottomNavigationHidden(requireActivity(), 150)
        val root = inflater.inflate(R.layout.fragment_default_guide, container, false)
        tool = root.findViewById<Toolbar>(R.id.toolbar2)
        textWhatIsTrash = root.findViewById(R.id.guide_text_what_is_trash)
        textWhatIsNotTrash = root.findViewById(R.id.guide_text_what_is_not_trash)


        tool.setNavigationIcon(R.drawable.arrow_back_v2)



        tool.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val type = getGuideType()
        val title = when(type){
            TrashTypes.PAPER->R.string.type_paper
            TrashTypes.GLASS->R.string.type_glass
            TrashTypes.PLASTIC->R.string.type_plastic
            TrashTypes.METAL->R.string.type_metal
            else->R.string.type_paper
        }
        tool.title = requireContext().resources.getString(title)

        tool.setTitleTextColor(requireContext().resources.getColor(R.color.white))

       when(type){
           TrashTypes.PAPER->{
               textWhatIsTrash.text=requireContext().resources.getString(R.string.what_is_trash_paper)
               textWhatIsNotTrash.text=requireContext().resources.getString(R.string.what_is_not_trash_paper)
           }

           TrashTypes.GLASS->{
               textWhatIsTrash.text=requireContext().resources.getString(R.string.what_is_trash_glass)
               textWhatIsNotTrash.text=requireContext().resources.getString(R.string.what_is_not_trash_glass)
           }

           TrashTypes.PLASTIC->{
               textWhatIsTrash.text=requireContext().resources.getString(R.string.what_is_trash_plastic)
               textWhatIsNotTrash.text=requireContext().resources.getString(R.string.what_is_not_trash_plastic)
           }

           TrashTypes.METAL->{
               textWhatIsTrash.text=requireContext().resources.getString(R.string.what_is_trash_metal)
               textWhatIsNotTrash.text=requireContext().resources.getString(R.string.what_is_not_trash_metal)
           }

           else -> {}
       }


        return root
    }

    private fun getGuideType(): TrashTypes {

        if (requireArguments().getBoolean(R.string.type_paper.toString())) {
            return TrashTypes.PAPER
        }

        if (requireArguments().getBoolean(R.string.type_glass.toString())) {
            return TrashTypes.GLASS
        }

        if (requireArguments().getBoolean(R.string.type_plastic.toString())) {
            return TrashTypes.PLASTIC
        }

        if (requireArguments().getBoolean(R.string.type_metal.toString())) {
            return TrashTypes.METAL
        }
        return TrashTypes.DEFAULT
    }


    override fun onBackPressed(): Boolean {
        ViewUtils.bottomNavigationShowed(requireActivity(), 100)
        App.getComponent()
            .setActiveFragment(parentFragmentManager.findFragmentByTag("GUIDE_FRAG")!!)
        return true
    }


}