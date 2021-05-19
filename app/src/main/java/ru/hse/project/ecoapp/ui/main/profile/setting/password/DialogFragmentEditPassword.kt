package ru.hse.project.ecoapp.ui.main.profile.setting.password

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
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.util.ViewModelFactory

class DialogFragmentEditPassword : DialogFragment() {
    private lateinit var editOldPassword: EditText
    private lateinit var editNewPassword: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnComplete: Button
    private lateinit var editPasswordViewModel: EditPasswordViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_fragment_edit_password, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        editOldPassword = root.findViewById(R.id.edit_password_old_field)
        editNewPassword = root.findViewById(R.id.edit_password_new_field)
        btnCancel = root.findViewById(R.id.edit_password_cancel_btn)
        btnComplete = root.findViewById(R.id.edit_password_save_btn)
        editPasswordViewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(EditPasswordViewModel::class.java)


        editPasswordViewModel.editFormStateOldPassword.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                editOldPassword.error = if (editState.error != null) {
                    getString(editState.error)
                } else {
                    null
                }
            })


        editOldPassword.afterTextChanged {
            editPasswordViewModel.dataChangedOldPassword(editOldPassword.text.toString())
        }

        editPasswordViewModel.editFormStateNewPassword.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                editNewPassword.error = if (editState.error != null) {
                    getString(editState.error)
                } else {
                    null
                }
            })


        editNewPassword.afterTextChanged {
            editPasswordViewModel.dataChangedNewPassword(editNewPassword.text.toString())
        }


        editPasswordViewModel.editPasswordFormResult.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                if (editState.isDataValid) {
                    editPasswordViewModel.updatePassword(editNewPassword.text.toString())
                }
            })

        btnComplete.setOnClickListener {
            editPasswordViewModel.checkAllData(
                editOldPassword.text.toString(),
                editNewPassword.text.toString()
            )
        }

        btnCancel.setOnClickListener {
            dismiss()
        }


        editPasswordViewModel.editPasswordResult.observe(viewLifecycleOwner,
            Observer {
                dismiss()
                val editResult = it ?: return@Observer
                if (editResult.isSuccess) {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.update_password),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.invalid_update_password),
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