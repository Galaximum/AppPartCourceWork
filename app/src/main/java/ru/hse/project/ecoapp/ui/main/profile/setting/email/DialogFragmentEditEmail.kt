package ru.hse.project.ecoapp.ui.main.profile.setting.email

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.util.ViewModelFactory


class DialogFragmentEditEmail : DialogFragment() {


    private lateinit var editEmail: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnComplete: Button
    private lateinit var editEmailViewModel: EditEmailViewModel
    private val repository = App.getComponent().getRepository()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.dialog_fragment_edit_email, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        editEmail = root.findViewById(R.id.edit_email_field)
        btnCancel = root.findViewById(R.id.edit_email_cancel_btn)
        btnComplete = root.findViewById(R.id.edit_email_save_btn)
        editEmailViewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(EditEmailViewModel::class.java)
        editEmail.setText(repository.user!!.email)


        editEmailViewModel.editFormStateEmail.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                editEmail.error = if (editState.error != null) {
                    getString(editState.error)
                } else {
                    null
                }
            })


        editEmail.afterTextChanged {
            editEmailViewModel.dataChangedEmail(editEmail.text.toString())
        }


        editEmailViewModel.editEmailFormResult.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                if (editState.isDataValid) {
                    editEmailViewModel.updateEmail(editEmail.text.toString())
                }
            })

        btnComplete.setOnClickListener {
            editEmailViewModel.checkAllData(editEmail.text.toString())
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        editEmailViewModel.editEmailResult.observe(viewLifecycleOwner,
            Observer {
                dismiss()
                val editResult = it ?: return@Observer
                if (editResult.isSuccess) {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.update_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.invalid_update_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

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