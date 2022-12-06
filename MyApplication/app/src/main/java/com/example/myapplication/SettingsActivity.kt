package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.example.myapplication.ui.home.CustomRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /*
        val lv: ListView = findViewById(R.id.settings)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("Редактировать данные", "Выход"))
        lv.adapter = adapter
         */

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
}