package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

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

        val btnBack: ImageButton = findViewById(R.id.backFromProduct)
        btnBack.setOnClickListener {
            finish()
        }


    }
}