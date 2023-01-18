package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserDataActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userSurname: EditText
    private lateinit var userPhone: EditText

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        userName = findViewById(R.id.etUserName)
        userSurname = findViewById(R.id.etUserSurname)
        userPhone = findViewById(R.id.etUserPhone)
        val btn: Button = findViewById(R.id.btnSaveData)

        val userLog = Firebase.auth.currentUser

        database = Firebase.database.reference
        database.child("Users").child(userLog?.uid!!).setValue(User(userLog?.uid!!,"","", "", mutableListOf<Product>(), "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"))

        btn.setOnClickListener {
            SetData()
            fun View.hideKeyboard() {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            userName.hideKeyboard()
            userSurname.hideKeyboard()
            userPhone.hideKeyboard()
        }

        userName.setOnKeyListener (View.OnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) and
                (i == KeyEvent.KEYCODE_ENTER)) {
                userName.requestFocus()
                userName.setFocusableInTouchMode(true)

                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(userName, InputMethodManager.SHOW_FORCED)

                true
            }
            false
        })
    }

    fun SetData() {
        val name = userName.text.toString()
        val surname = userSurname.text.toString()
        val phone = userPhone.text.toString()

        if (name.isBlank() || surname.isBlank() || phone.isBlank()){
            Toast.makeText(this, "Укажите все данные", Toast.LENGTH_SHORT).show()
            return
        }

        val userLog = Firebase.auth.currentUser
        database = Firebase.database.reference

        database.child("Users").child(userLog?.uid!!).child("name").setValue(name)
        database.child("Users").child(userLog?.uid!!).child("surname").setValue(surname)
        database.child("Users").child(userLog?.uid!!).child("phone").setValue(phone)

        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
        val start = Intent(this, MainActivity::class.java)
        startActivity(start)
    }
}