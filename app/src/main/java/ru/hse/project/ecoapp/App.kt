package ru.hse.project.ecoapp

import android.app.Application
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import ru.hse.project.ecoapp.model.AppComponent
import ru.hse.project.ecoapp.ui.main.map.YandexMapWrapper
import kotlin.concurrent.thread

class App : Application() {

    companion object {
        const val API_KEY = "1dfa139a-1aaa-41ee-9e76-4f83d3cb18b4"

        private lateinit var component: AppComponent
        lateinit var initData: Task<Void>
        fun getComponent(): AppComponent {
            return component
        }
    }

    override fun onCreate() {
        val task = TaskCompletionSource<Void>()
        thread {
            component = AppComponent(this)
            YandexMapWrapper.initialize(this, API_KEY)
            task.setResult(null)
        }
        initData = task.task

        super.onCreate()
    }

}