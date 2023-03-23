package com.example.myapplication

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference

data class Product(val uid: String,
                   val prodId: String,
                   val name: String?,
                   val cost: String?,
                   val type: String?,
                   val description: String?,
                   val location: String?,
                   val image: String? = "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"
)
data class AddProduct(val name: String?,
                      val cost: String?,
                      val type: String?,
                      val description: String?,
                      val location: String?,
                      var image: String? = "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"
)
data class User(val uid: String,
                val name: String?,
                val surname: String?,
                val phone: String?,
                val products: List<Product>,
                val avatar: String? = "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/def.jpg?alt=media&token=7e24bbcf-a4d7-4020-b501-94049d30f77d"
)
data class Wish(val uid: String,
                val prodId: String,
                val request: Boolean = false
)

//val listType = listOf<String>("Обувь", "Одежда", "Для дома", "Мебель", "Для детей", "Другое")
val listWishes = mutableListOf<Wish>()
val listProduct = mutableListOf<Product>()
val indexH = mutableListOf<Int>()
var kategory = ""

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        //supportActionBar?.setBackgroundDrawable(resources.getColor(R.color.secondColor).toDrawable())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

/*    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_account, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.navigation_to_settings -> {
            Toast.makeText(this, "ок", Toast.LENGTH_SHORT).show()
            true
        }
        else -> {
            true
        }
    }*/
}