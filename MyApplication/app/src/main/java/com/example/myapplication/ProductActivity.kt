package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.gradient_blue))
        supportActionBar?.title = intent.getStringExtra("name")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val nameProduct = intent.getStringExtra("name")
        val costProduct = intent.getStringExtra("cost")
        val imgProduct = intent.getStringExtra("img")
        val descriptionProduct = intent.getStringExtra("description")
        val locationProduct = intent.getStringExtra("location")

        val tvNameProduct: TextView = findViewById(R.id.nameProduct)
        val tvCostProduct: TextView = findViewById(R.id.costProduct)
        val image: ImageView = findViewById(R.id.imgProduct)

        tvNameProduct.text = nameProduct
        tvCostProduct.text = costProduct
        Picasso.get().load(imgProduct).into(image)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}