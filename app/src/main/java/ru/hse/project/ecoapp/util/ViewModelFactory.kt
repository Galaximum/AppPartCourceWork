package ru.hse.project.ecoapp.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.ui.auth.emailAuth.login.LoginViewModel
import ru.hse.project.ecoapp.ui.auth.emailAuth.registration.RegistrationViewModel
import ru.hse.project.ecoapp.ui.main.map.addMarker.AddMarkerViewModel
import ru.hse.project.ecoapp.ui.main.map.search.SearchViewModel
import ru.hse.project.ecoapp.ui.main.profile.setting.email.EditEmailViewModel
import ru.hse.project.ecoapp.ui.main.profile.setting.nickname.EditNickNameViewModel
import ru.hse.project.ecoapp.ui.main.profile.setting.password.EditPasswordViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(App.getComponent().getRepository()) as T
        }

        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(App.getComponent().getRepository()) as T

        }

        if (modelClass.isAssignableFrom(EditNickNameViewModel::class.java)) {
            return EditNickNameViewModel(App.getComponent().getRepository()) as T

        }

        if (modelClass.isAssignableFrom(EditEmailViewModel::class.java)) {
            return EditEmailViewModel(App.getComponent().getRepository()) as T

        }

        if (modelClass.isAssignableFrom(EditPasswordViewModel::class.java)) {
            return EditPasswordViewModel(App.getComponent().getRepository()) as T

        }

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(App.getComponent().getRepository()) as T

        }

        if (modelClass.isAssignableFrom(AddMarkerViewModel::class.java)) {
            return AddMarkerViewModel(App.getComponent().getRepository()) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}