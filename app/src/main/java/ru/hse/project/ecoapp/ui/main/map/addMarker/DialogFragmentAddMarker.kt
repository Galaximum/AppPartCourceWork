package ru.hse.project.ecoapp.ui.main.map.addMarker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yandex.mapkit.geometry.Point
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.util.ViewModelFactory

class DialogFragmentAddMarker(private val point: Point) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_fragment_add_new_marker, container, false)
        //  dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layTitle: TextInputLayout = root.findViewById(R.id.add_marker_text_lay_title)
        val layAddress: TextInputLayout = root.findViewById(R.id.add_marker_text_lay_adress)
        val textTitle: TextInputEditText = root.findViewById(R.id.add_marker_edit_text_title)
        val textAddress: TextInputEditText = root.findViewById(R.id.add_marker_edit_text_address)
        val switchPaper: SwitchMaterial = root.findViewById(R.id.add_marker_switch_paper)
        val switchGlass: SwitchMaterial = root.findViewById(R.id.add_marker_switch_glass)
        val switchPlastic: SwitchMaterial = root.findViewById(R.id.add_marker_switch_plastic)
        val switchMetal: SwitchMaterial = root.findViewById(R.id.add_marker_switch_metal)
        val btnConfirm: Button = root.findViewById(R.id.add_marker_btn_confirm)
        val viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(AddMarkerViewModel::class.java)

        viewModel.editTitleState.observe(
            viewLifecycleOwner,
            Observer {
                val state = it ?: return@Observer
                layTitle.error = if (state.error != null) {
                    getString(state.error)
                } else {
                    null
                }
            })

        viewModel.editAddressState.observe(
            viewLifecycleOwner,
            Observer {
                val state = it ?: return@Observer
                layAddress.error = if (state.error != null) {
                    getString(state.error)
                } else {
                    null
                }
            })


        viewModel.stateResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer
            if (result.isDataValid) {
                viewModel.addNewMarker(
                    point,
                    textTitle.text.toString(),
                    textAddress.text.toString(),
                    switchPaper.isChecked,
                    switchGlass.isChecked,
                    switchPlastic.isChecked,
                    switchMetal.isChecked
                )
            } else {
                Toast.makeText(
                    context,
                    context?.resources?.getString(result.error!!),
                    Toast.LENGTH_SHORT
                ).show()
            }


        })

        viewModel.addResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer
            dismiss()
            if (result.success) {
                Toast.makeText(
                    context,
                    "The application has been sent successful",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    result.error!!,
                    Toast.LENGTH_LONG
                ).show()
            }

        })


        textTitle.afterTextChanged {
            viewModel.dataChangedTitle(textTitle.text.toString())
        }

        textAddress.afterTextChanged {
            viewModel.dataChangedAddress(textAddress.text.toString())
        }


        btnConfirm.setOnClickListener {
            viewModel.checkAllData(
                textTitle.text.toString(),
                textAddress.text.toString(),
                switchPaper.isChecked,
                switchGlass.isChecked,
                switchPlastic.isChecked,
                switchMetal.isChecked
            )
        }

        return root
    }


    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

}