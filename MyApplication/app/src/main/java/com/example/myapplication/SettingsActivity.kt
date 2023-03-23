package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val btnExit: Button = findViewById(R.id.btnExit)
        btnExit.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            //val userLog = Firebase.auth.currentUser
            startActivity(Intent(this, StartActivity::class.java))
            finish()
            listWishes.clear()
            listProduct.clear()
            indexH.clear()
            kategory = ""
        }

        val btnChange: Button = findViewById(R.id.btnChange)
        btnChange.setOnClickListener {
            startActivity(Intent(this, ChangeDataActivity::class.java))
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}