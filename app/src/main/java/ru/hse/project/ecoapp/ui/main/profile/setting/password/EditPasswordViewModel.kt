package ru.hse.project.ecoapp.ui.main.profile.setting.password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.model.FormState
import ru.hse.project.ecoapp.util.Validator

class EditPasswordViewModel(private val repository: Repository) : ViewModel() {
    private val _editFormStateOldPassword = MutableLiveData<FormState>()
    val editFormStateOldPassword: LiveData<FormState> =
        _editFormStateOldPassword
    private val _editFormStateNewPassword = MutableLiveData<FormState>()
    val editFormStateNewPassword: LiveData<FormState> =
        _editFormStateNewPassword
    private val _editPasswordFormResult = MutableLiveData<FormState>()
    val editPasswordFormResult: LiveData<FormState> = _editPasswordFormResult
    private val _editPasswordResult = MutableLiveData<Result<Void>>()
    val editPasswordResult: LiveData<Result<Void>> = _editPasswordResult
    private val oldPassword = repository.user!!.password


    fun dataChangedOldPassword(oldPassword: String) {
        _editFormStateOldPassword.value =
            if (oldPassword!=this.oldPassword) {
                FormState(error = R.string.invalid_old_password_not_old)
            } else {
                FormState(isDataValid = true)
            }
    }


    fun dataChangedNewPassword(newPassword: String) {
        _editFormStateNewPassword.value = if (oldPassword == newPassword) {
            FormState(error = R.string.invalid_new_password_is_old)
        } else {
            when (val validResult = Validator.passwordValidation(newPassword)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
        }
    }


    fun checkAllData(
        oldPassword: String,
        newPassword: String
    ) {
        dataChangedNewPassword(oldPassword)
        dataChangedNewPassword(newPassword)

        _editPasswordFormResult.value =
            if (_editFormStateOldPassword.value!!.isDataValid
                && _editFormStateNewPassword.value!!.isDataValid
            ) {
                FormState(isDataValid = true)
            } else {
                FormState(error = null)
            }
    }


    fun updatePassword(newPassword: String) {
        repository.user!!.updatePassword(newPassword).addOnSuccessListener {
            _editPasswordResult.value = Result.success(it)
        }.addOnFailureListener {
            _editPasswordResult.value = Result.failure(it)
        }

    }

    private fun isOldPasswordValid(oldPassword: String): Boolean {
        return oldPassword == this.oldPassword
    }

}