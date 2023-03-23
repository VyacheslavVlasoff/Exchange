package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.account.AccountFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ChangeDataActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_data)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val userLog = Firebase.auth.currentUser
        database = Firebase.database.reference

        val etName: EditText = findViewById(R.id.etName)
        val etSurname: EditText = findViewById(R.id.etSurname)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val img: ImageView = findViewById(R.id.profile_image_change)

        database.child("Users").child(userLog?.uid!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                Picasso.get().load(snapshot.child("avatar").getValue(String::class.java)).into(img)
                etName.setText(snapshot.child("name").getValue(String::class.java))
                etSurname.setText(snapshot.child("surname").getValue(String::class.java))
                etPhone.setText(snapshot.child("phone").getValue(String::class.java))
                etEmail.setText(userLog.email)
            }
        }

        val btnChangeData: Button = findViewById(R.id.btnSaveChangedData)
        btnChangeData.setOnClickListener {
            database.child("Users").child(userLog?.uid!!).child("name").setValue(etName.text.toString())
            database.child("Users").child(userLog?.uid!!).child("surname").setValue(etSurname.text.toString())
            database.child("Users").child(userLog?.uid!!).child("phone").setValue(etPhone.text.toString())
            Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()
            fun View.hideKeyboard() {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            etName.hideKeyboard()
            etSurname.hideKeyboard()
            etPhone.hideKeyboard()
            etEmail.hideKeyboard()
            fragmentManager.findFragmentById(R.id.navigation_account)
            //MainActivity().navigateUpTo(Intent(this, AccountFragment::class.java))
            finish()
        }

        etName.setOnKeyListener (View.OnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) and
                (i == KeyEvent.KEYCODE_ENTER)) {
                etName.requestFocus()
                etName.setFocusableInTouchMode(true)

                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etName, InputMethodManager.SHOW_FORCED)

                true
            }
            false
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}