package com.example.myapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StartActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //FirebaseAuth.getInstance().signOut()
        val userLog = Firebase.auth.currentUser
        database = Firebase.database.reference
        var snapshot: DataSnapshot
        if (userLog != null) {
            database.child("Users").child(userLog?.uid!!).child("name").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    snapshot = task.result
                    if (snapshot.getValue(String::class.java) != "")
                        startActivity(Intent(this, MainActivity::class.java))
                    else
                        startActivity(Intent(this, UserDataActivity::class.java))
                    finish()
                    }
            }
        }
        else setContentView(R.layout.activity_start)


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