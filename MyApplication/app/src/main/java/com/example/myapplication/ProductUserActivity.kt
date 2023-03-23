package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlin.concurrent.fixedRateTimer

class ProductUserActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_user)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        database = Firebase.database.reference

        val nameProduct = intent.getStringExtra("name")
        val costProduct = intent.getStringExtra("cost")
        val imgProduct = intent.getStringExtra("img")
        val descriptionProduct = intent.getStringExtra("description")
        val locationProduct = intent.getStringExtra("location")

        val tvNameProduct: TextView = findViewById(R.id.nameProductUser)
        val tvCostProduct: TextView = findViewById(R.id.costProductUser)
        val image: ImageView = findViewById(R.id.imgProductUser)
        val description: TextView = findViewById(R.id.descriptionProductUser)
        val location: TextView = findViewById(R.id.locationProduct)

        tvNameProduct.text = nameProduct
        tvCostProduct.text = costProduct
        Picasso.get().load(imgProduct).into(image)
        description.text = descriptionProduct
        location.text = locationProduct

        val btnChange: TextView = findViewById(R.id.btnChangeProduct)
        val btnDelete: TextView = findViewById(R.id.btnDeleteProduct)

        btnChange.setOnClickListener {
            startActivity(Intent(this, ChangeProductActivity::class.java).apply {
                putExtra("name", tvNameProduct.text)
                putExtra("cost", tvCostProduct.text)
                putExtra("img", imgProduct)
                putExtra("description", description.text)
                putExtra("location", location.text)
            putExtra("uid", intent.getStringExtra("uid"))})
        }

        btnDelete.setOnClickListener {
            var userLog = Firebase.auth.currentUser
            database.child("Users").child(userLog!!.uid).child("products")
                .child(intent.getStringExtra("uid")!!).removeValue()
            Toast.makeText(this, "Объявление удалено", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}