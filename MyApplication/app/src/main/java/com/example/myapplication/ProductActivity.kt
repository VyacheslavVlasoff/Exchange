package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
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
        val description: TextView = findViewById(R.id.descriptionProduct)

        tvNameProduct.text = nameProduct
        tvCostProduct.text = costProduct
        Picasso.get().load(imgProduct).into(image)
        description.text = descriptionProduct
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}