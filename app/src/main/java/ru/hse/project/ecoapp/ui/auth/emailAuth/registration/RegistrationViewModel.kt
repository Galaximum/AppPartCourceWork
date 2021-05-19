package ru.hse.project.ecoapp.ui.auth.emailAuth.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.model.FormState
import ru.hse.project.ecoapp.model.TResult
import ru.hse.project.ecoapp.util.Validator


class RegistrationViewModel(private val repository: Repository) : ViewModel() {

    private val _registrationFormStateNickName = MutableLiveData<FormState>()
    val registrationFormStateNickName: LiveData<FormState> =
        _registrationFormStateNickName
    private val _registrationFormStateFirstName = MutableLiveData<FormState>()
    val registrationFormStateFirstName: LiveData<FormState> =
        _registrationFormStateFirstName
    private val _registrationFormStateSecondName = MutableLiveData<FormState>()
    val registrationFormStateSecondName: LiveData<FormState> =
        _registrationFormStateSecondName
    private val _registrationFormStateEmail = MutableLiveData<FormState>()
    val registrationFormStateEmail: LiveData<FormState> =
        _registrationFormStateEmail
    private val _registrationFormStatePassword = MutableLiveData<FormState>()
    val registrationFormStatePassword: LiveData<FormState> =
        _registrationFormStatePassword
    private val _registrationFormStateResult = MutableLiveData<FormState>()
    val registrationFormStateResult: LiveData<FormState> =
        _registrationFormStateResult

    private val _registrationResult = MutableLiveData<TResult>()
    val registrationResult: LiveData<TResult> = _registrationResult

    fun registration(
        nickName: String,
        firstName: String,
        secondName: String,
        email: String,
        password: String
    ) {

        val result = repository.registration(nickName, firstName, secondName, email, password)

        result.addOnSuccessListener {
            _registrationResult.value = TResult(success = true)
        }.addOnFailureListener {

            _registrationResult.value = TResult(error = it.message)
        }

    }

    fun dataChangedNickName(nickName: String) {
        _registrationFormStateNickName.value =
            when (val validResult = Validator.nickNameValidation(nickName)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }

    fun dataChangedFirstName(firstName: String) {
        _registrationFormStateFirstName.value =
            when (val validResult = Validator.userNameValidation(firstName)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }

    fun dataChangedSecondName(secondName: String) {
        _registrationFormStateSecondName.value =
            when (val validResult = Validator.userNameValidation(secondName)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }


    fun dataChangedEmail(email: String) {
        _registrationFormStateEmail.value =
            when (val validResult = Validator.emailValidation(email)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }

    fun dataChangedPassword(password: String) {
        _registrationFormStatePassword.value =
            when (val validResult = Validator.passwordValidation(password)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }


    fun checkAllData(
        nickName: String,
        firstName: String,
        secondName: String,
        email: String,
        password: String
    ) {
        dataChangedNickName(nickName)
        dataChangedFirstName(firstName)
        dataChangedSecondName(secondName)
        dataChangedEmail(email)
        dataChangedPassword(password)

        _registrationFormStateResult.value =
            if (
                _registrationFormStateNickName.value!!.isDataValid
                && _registrationFormStateFirstName.value!!.isDataValid
                && _registrationFormStateSecondName.value!!.isDataValid
                && _registrationFormStateEmail.value!!.isDataValid
                && _registrationFormStatePassword.value!!.isDataValid
            ) {
                FormState(isDataValid = true)
            } else {
                FormState(error = R.string.invalid_fields)
            }
    }


}