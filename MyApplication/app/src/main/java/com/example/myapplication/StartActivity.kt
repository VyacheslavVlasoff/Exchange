package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    fun GetStart(view: View) {
        val start = Intent(this, LoginActivity::class.java)
        startActivity(start)
    }

    fun SignUp(view: View) {
        val signUp = Intent(this, SignUpActivity::class.java)
        startActivity(signUp)
    }
}