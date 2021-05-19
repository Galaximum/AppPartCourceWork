package ru.hse.project.ecoapp.ui.main.map.addMarker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.data.Repository
import ru.hse.project.ecoapp.model.FormState
import ru.hse.project.ecoapp.model.TResult
import ru.hse.project.ecoapp.util.Validator

class AddMarkerViewModel(private val repository: Repository) : ViewModel() {


    private val _stateResult = MutableLiveData<FormState>()
    val stateResult: LiveData<FormState> = _stateResult

    private val _editTitleState = MutableLiveData<FormState>()
    val editTitleState: LiveData<FormState> = _editTitleState

    private val _editAddressState = MutableLiveData<FormState>()
    val editAddressState: LiveData<FormState> = _editAddressState

    private val _addResult = MutableLiveData<TResult>()
    val addResult: LiveData<TResult> = _addResult


    fun dataChangedTitle(title: String) {
        _editTitleState.value =
            when (val validResult = Validator.titleValidation(title)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }


    fun dataChangedAddress(address: String) {
        _editAddressState.value =
            when (val validResult = Validator.addressValidation(address)) {
                0 -> FormState(isDataValid = true)
                else -> FormState(error = validResult)
            }
    }


    fun checkAllData(
        title: String,
        address: String,
        isPaper: Boolean,
        isGlass: Boolean,
        isPlastic: Boolean,
        isMetal: Boolean,
    ) {

        dataChangedTitle(title)
        dataChangedAddress(address)

        _stateResult.value =
            if (
                _editTitleState.value!!.isDataValid
                && _editAddressState.value!!.isDataValid
            ) {
                if (!(isPaper || isGlass || isPlastic || isMetal)) {
                    FormState(error = R.string.invalid_types)
                } else {
                    FormState(isDataValid = true)
                }
            } else {
                FormState(error = R.string.invalid_fields)
            }
    }


    fun addNewMarker(
        point: Point,
        title: String,
        address: String,
        isPaper: Boolean,
        isGlass: Boolean,
        isPlastic: Boolean,
        isMetal: Boolean
    ) {
        repository.addNewMarker(point, title, address, isPaper, isGlass, isPlastic, isMetal)
            .addOnSuccessListener {
                _addResult.value = TResult(success = true)
            }.addOnFailureListener {
            _addResult.value = TResult(error = it.message)
        }
    }


}