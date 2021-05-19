package ru.hse.project.ecoapp.ui.main.profile.setting.email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.model.FormState
import ru.hse.project.ecoapp.util.Validator

class EditEmailViewModel(private val repository: Repository) : ViewModel() {
    private val _editFormStateEmail = MutableLiveData<FormState>()
    val editFormStateEmail: LiveData<FormState> =
        _editFormStateEmail
    private val _editEmailFormResult = MutableLiveData<FormState>()
    val editEmailFormResult: LiveData<FormState> = _editEmailFormResult
    private val _editEmailResult = MutableLiveData<Result<Void>>()
    val editEmailResult: LiveData<Result<Void>> = _editEmailResult
    private val oldEmail = repository.user!!.email

    fun dataChangedEmail(email: String) {
        _editFormStateEmail.value =
            if (oldEmail == email) {
                FormState(error = R.string.invalid_email_similar_old_email)
            } else {
                when (val validResult = Validator.emailValidation(email)) {
                    0 -> FormState(isDataValid = true)
                    else -> FormState(error = validResult)
                }
            }
    }


    fun checkAllData(
        email: String,
    ) {
        dataChangedEmail(email)
        _editEmailFormResult.value =
            if (_editFormStateEmail.value!!.isDataValid) {
                FormState(isDataValid = true)
            } else {
                FormState(error = null)
            }
    }


    fun updateEmail(email: String) {
        repository.user!!.updateEmail(email).addOnSuccessListener {
            _editEmailResult.value = Result.success(it)
        }.addOnFailureListener {
            _editEmailResult.value = Result.failure(it)
        }
    }

}