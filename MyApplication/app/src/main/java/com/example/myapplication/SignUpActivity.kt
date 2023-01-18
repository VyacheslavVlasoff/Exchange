package com.example.myapplication

import android.R.attr.accountType
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.account.AccountFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent

class SignUpActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
    lateinit var tvRedirectLogin: TextView

    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth
    //private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // View Bindings
        etEmail = findViewById(R.id.etSEmailAddress)
        etConfPass = findViewById(R.id.etSConfPassword)
        etPass = findViewById(R.id.etSPassword)
        btnSignUp = findViewById(R.id.btnSSigned)
        tvRedirectLogin = findViewById(R.id.tvRedirectLogin)

        // Initialising auth object
        auth = Firebase.auth

        btnSignUp.setOnClickListener {
            signUpUser()
        }

        // switching from signUp Activity to Login Activity
        tvRedirectLogin.setOnClickListener {
            fun View.hideKeyboard() {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            etEmail.hideKeyboard()
            etConfPass.hideKeyboard()
            etPass.hideKeyboard()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun signUpUser() {
        val name = "Ivan"
        val surname = "Ivanov"
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()

        // Obtain the FirebaseAnalytics instance.
        //firebaseAnalytics = Firebase.analytics

        // check pass
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email и пароль не могут быть пустыми", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT)
                .show()
            return
        }
        // If all credential are correct
        // We call createUserWithEmailAndPassword
        // using auth object and pass the
        // email and pass in it.
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                /*firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    //param(FirebaseAnalytics.Param.ITEM_NAME, name)
                    //param(FirebaseAnalytics.Param.ITEM_NAME, surname)
                    param(FirebaseAnalytics.Param.CONTENT_TYPE, "Image")
                }*/
                //firebaseAnalytics.setUserProperty("Name", name)
                //firebaseAnalytics.setUserProperty("Surname",surname)
                //firebaseAnalytics.setUserProperty("Image", "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d")
                Toast.makeText(this, "Успешная регистрация", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserDataActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Ошибка при регистрации!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
