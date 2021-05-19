package ru.hse.project.ecoapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ru.hse.project.ecoapp.ui.auth.emailAuth.DialogFragmentEmailAuth

class AuthActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        findViewById<Button>(R.id.emailAuth).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.emailAuth -> DialogFragmentEmailAuth().show(supportFragmentManager, "FFF")
        }
    }

}


