package ru.hse.project.ecoapp.ui.auth.emailAuth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.model.FormState
import ru.hse.project.ecoapp.model.TResult
import ru.hse.project.ecoapp.util.Validator

class LoginViewModel(private val repository: Repository) : ViewModel() {


    private val _loginFormStateEmail = MutableLiveData<FormState>()
    val loginFormStateEmail: LiveData<FormState> =
        _loginFormStateEmail
    private val _loginFormStatePassword = MutableLiveData<FormState>()
    val loginFormStatePassword: LiveData<FormState> =
        _loginFormStatePassword
    private val _loginFormStateResult = MutableLiveData<FormState>()
    val loginFormStateResult: LiveData<FormState> =
        _loginFormStateResult


    private val _loginResult = MutableLiveData<TResult>()
    val loginResult: LiveData<TResult> = _loginResult


    fun signIn(email: String, password: String) {
        val result = repository.signIn(email, password)
        result.addOnSuccessListener {
            _loginResult.value = TResult(success = true)
        }.addOnFailureListener {
            _loginResult.value = TResult(error = it.message)
        }
    }

    fun dataChangedEmail(email: String) {
        _loginFormStateEmail.value = when (val validResult = Validator.emailValidation(email)) {
            0 -> FormState(isDataValid = true)
            else -> FormState(error = validResult)
        }
    }

    fun dataChangedPassword(password: String) {
        _loginFormStatePassword.value =
            when (val validResult = Validator.passwordValidation(password)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }


    fun checkAllData(
        email: String,
        password: String
    ) {
        dataChangedEmail(email)
        dataChangedPassword(password)
        _loginFormStateResult.value =
            if (
                _loginFormStateEmail.value!!.isDataValid
                && _loginFormStatePassword.value!!.isDataValid
            ) {
                FormState(isDataValid = true)
            } else {
                FormState(error = R.string.invalid_fields)
            }
    }


}