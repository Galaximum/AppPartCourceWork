package ru.hse.project.ecoapp.ui.auth.emailAuth.registration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import ru.hse.project.ecoapp.MainActivity
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.util.ViewModelFactory

class EmailRegistrationFragment : Fragment() {


    private lateinit var registrationViewModel: RegistrationViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_email_registration, container, false)

        val layNickName = root.findViewById<TextInputLayout>(R.id.reg_text_input_lay_nick_name)
        val nickName = root.findViewById<EditText>(R.id.reg_text_input_nick_name)
        val layFirstName = root.findViewById<TextInputLayout>(R.id.reg_text_input_lay_first_name)
        val firstName = root.findViewById<EditText>(R.id.reg_text_input_first_name)
        val laySecondName = root.findViewById<TextInputLayout>(R.id.reg_text_input_lay_second_name)
        val secondName = root.findViewById<EditText>(R.id.reg_text_input_second_name)
        val layEmail = root.findViewById<TextInputLayout>(R.id.reg_text_input_lay_email)
        val email = root.findViewById<EditText>(R.id.reg_text_input_email)
        val layPassword = root.findViewById<TextInputLayout>(R.id.reg_text_input_lay_password)
        val password = root.findViewById<EditText>(R.id.reg_text_input_password)
        val buttonRegister = root.findViewById<Button>(R.id.reg_btn_register)
        registrationViewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(RegistrationViewModel::class.java)


        registrationViewModel.registrationFormStateNickName.observe(
            viewLifecycleOwner,
            Observer {
                val registrationState = it ?: return@Observer
                layNickName.error = if (registrationState.error != null) {
                    getString(registrationState.error)
                } else {
                    null
                }
            })
        nickName.afterTextChanged {
            registrationViewModel.dataChangedNickName(nickName.text.toString())
        }


        registrationViewModel.registrationFormStateFirstName.observe(
            viewLifecycleOwner,
            Observer {
                val registrationState = it ?: return@Observer
                layFirstName.error = if (registrationState.error != null) {
                    getString(registrationState.error)
                } else {
                    null
                }
            })

        firstName.afterTextChanged {
            registrationViewModel.dataChangedFirstName(firstName.text.toString())
        }

        registrationViewModel.registrationFormStateSecondName.observe(
            viewLifecycleOwner,
            Observer {
                val registrationState = it ?: return@Observer
                laySecondName.error = if (registrationState.error != null) {
                    getString(registrationState.error)
                } else {
                    null
                }
            })
        secondName.afterTextChanged {
            registrationViewModel.dataChangedSecondName(secondName.text.toString())
        }




        registrationViewModel.registrationFormStateEmail.observe(
            viewLifecycleOwner,
            Observer {
                val registrationState = it ?: return@Observer
                layEmail.error = if (registrationState.error != null) {
                    getString(registrationState.error)
                } else {
                    null
                }
            })
        email.afterTextChanged {
            registrationViewModel.dataChangedEmail(email.text.toString())
        }


        registrationViewModel.registrationFormStatePassword.observe(
            viewLifecycleOwner,
            Observer {
                val registrationState = it ?: return@Observer
                layPassword.error = if (registrationState.error != null) {
                    getString(registrationState.error)
                } else {
                    null
                }
            })
        password.afterTextChanged {
            registrationViewModel.dataChangedPassword(password.text.toString())
        }


        registrationViewModel.registrationFormStateResult.observe(
            viewLifecycleOwner,
            Observer {
                val registrationState = it ?: return@Observer
                if (registrationState.isDataValid) {
                    registrationViewModel.registration(
                        nickName.text.toString(),
                        firstName.text.toString(),
                        secondName.text.toString(),
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            })

        buttonRegister.setOnClickListener {
            registrationViewModel.checkAllData(
                nickName.text.toString(),
                firstName.text.toString(),
                secondName.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }



        registrationViewModel.registrationResult.observe(
            viewLifecycleOwner,
            Observer {
                val registrationResult = it ?: return@Observer

                if (registrationResult.success) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    if (activity != null) {
                        activity?.finishAfterTransition()
                    }
                } else {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.invalid_registration),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })

        return root;
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