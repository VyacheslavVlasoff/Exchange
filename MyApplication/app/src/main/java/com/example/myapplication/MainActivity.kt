package com.example.myapplication

import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.home.HomeFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

data class Product(val name: String?,
                   val cost: String?,
                   val type: String?,
                   val description: String?,
                   val location: String?,
                   val image: String? = "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"
)
data class User(val name: String?,
                val surname: String?,
                val phone: String?,
                val products: List<Product>,
                val avatar: String? = "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_katalog,
                R.id.navigation_wishes,
                R.id.navigation_message,
                R.id.navigation_account
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun toSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}