package ru.hse.project.ecoapp.ui.main.profile.setting.nickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.model.FormState
import ru.hse.project.ecoapp.util.Validator

class EditNickNameViewModel(private val repository: Repository) : ViewModel() {
    private val _editFormStateNickName = MutableLiveData<FormState>()
    val editFormStateFirstName: LiveData<FormState> =
        _editFormStateNickName
    private val _editNickNameFormResult = MutableLiveData<FormState>()
    val editNickNameFormResult: LiveData<FormState> = _editNickNameFormResult
    private val _editNickNameResult = MutableLiveData<Result<Void>>()
    val editNickNameResult: LiveData<Result<Void>> = _editNickNameResult
    private val oldNickName = repository.user!!.nickName


    fun dataChangedNickName(nickName: String) {
        _editFormStateNickName.value = if (oldNickName == nickName) {
            FormState(error = R.string.invalid_nick_name_is_old)
        } else {
            when (val validResult = Validator.nickNameValidation(nickName)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
        }
    }


    fun checkAllData(
        nickName: String,
    ) {
        dataChangedNickName(nickName)
        _editNickNameFormResult.value =
            if (_editFormStateNickName.value!!.isDataValid) {
                FormState(isDataValid = true)
            } else {
                FormState(error = null)
            }
    }


    fun updateNickName(nickName: String) {
        repository.user!!.updateNickName(nickName).addOnSuccessListener {
            _editNickNameResult.value = Result.success(it)
        }.addOnFailureListener {
            _editNickNameResult.value = Result.failure(it)
        }

    }


}