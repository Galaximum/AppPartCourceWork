package ru.hse.project.ecoapp.ui.auth.emailAuth.login

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

class EmailLoginFragment : Fragment() {


    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_email_login, container, false)

        val layEmail = root.findViewById<TextInputLayout>(R.id.log_text_input_lay_email)
        val email = root.findViewById<EditText>(R.id.log_text_input_email)
        val layPassword = root.findViewById<TextInputLayout>(R.id.log_text_input_lay_password)
        val password = root.findViewById<EditText>(R.id.log_text_input_password)
        val btnSignIn = root.findViewById<Button>(R.id.log_btn_sign_in)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(LoginViewModel::class.java)



        loginViewModel.loginFormStateEmail.observe(
            viewLifecycleOwner,
            Observer {
                val loginState = it ?: return@Observer
                layEmail.error = if (loginState.error != null) {
                    getString(loginState.error)
                } else {
                    null
                }
            })
        email.afterTextChanged {
            loginViewModel.dataChangedEmail(email.text.toString())
        }

        loginViewModel.loginFormStatePassword.observe(
            viewLifecycleOwner,
            Observer {
                val loginState = it ?: return@Observer
                layPassword.error = if (loginState.error != null) {
                    getString(loginState.error)
                } else {
                    null
                }
            })
        password.afterTextChanged {
            loginViewModel.dataChangedPassword(password.text.toString())
        }




        loginViewModel.loginFormStateResult.observe(viewLifecycleOwner, Observer {
            val loginStateResult = it ?: return@Observer
            if (loginStateResult.isDataValid) {
                loginViewModel.signIn(email.text.toString(), password.text.toString())
            }
        })


        btnSignIn.setOnClickListener {
            loginViewModel.checkAllData(email.text.toString(), password.text.toString())
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer


            if (loginResult.success) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                if (activity != null) {
                    activity?.finishAfterTransition()
                }
            } else {
                Toast.makeText(
                    context, requireContext().resources.getString(R.string.invalid_login),
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
        return root
    }


    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

}