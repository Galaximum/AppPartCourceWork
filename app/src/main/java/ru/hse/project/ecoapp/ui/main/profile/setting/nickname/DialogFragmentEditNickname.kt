package ru.hse.project.ecoapp.ui.main.profile.setting.nickname

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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.util.ViewModelFactory


class DialogFragmentEditNickname : DialogFragment() {


    private lateinit var editNickname: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnComplete: Button
    private lateinit var editNickNameViewModel: EditNickNameViewModel
    private val repository = App.getComponent().getRepository()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.dialog_fragment_edit_nickname, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        editNickname = root.findViewById(R.id.edit_nickname_field)
        btnCancel = root.findViewById(R.id.edit_nickname_cancel_btn)
        btnComplete = root.findViewById(R.id.edit_nickname_save_btn)
        editNickNameViewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(EditNickNameViewModel::class.java)
        editNickname.setText(repository.user!!.nickName)


        editNickNameViewModel.editFormStateFirstName.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                editNickname.error = if (editState.error != null) {
                    getString(editState.error)
                } else {
                    null
                }
            })


        editNickname.afterTextChanged {
            editNickNameViewModel.dataChangedNickName(editNickname.text.toString())
        }


        editNickNameViewModel.editNickNameFormResult.observe(
            viewLifecycleOwner,
            Observer {
                val editState = it ?: return@Observer
                if (editState.isDataValid) {
                   // dismiss()
                    editNickNameViewModel.updateNickName(editNickname.text.toString())
                }
            })

        btnComplete.setOnClickListener {
            editNickNameViewModel.checkAllData(editNickname.text.toString())
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        editNickNameViewModel.editNickNameResult.observe(viewLifecycleOwner,
            Observer {
                dismiss()
                val editResult = it ?: return@Observer
                if (editResult.isSuccess) {
                    Toast.makeText(context,
                        requireContext().resources.getString(R.string.update_nick_name),
                        Toast.LENGTH_SHORT).show()
                    requireActivity().findViewById<TextView>(R.id.profile_frag_nickname).text =
                        editNickname.text
                } else {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.invalid_update_nick_name),
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