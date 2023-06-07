package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.myapplication.ui.home.HomeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ProductActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
        val location: TextView = findViewById(R.id.locationProduct)

        tvNameProduct.text = nameProduct
        tvCostProduct.text = costProduct
        Picasso.get().load(imgProduct).into(image)
        description.text = descriptionProduct
        location.text = locationProduct

        val btn: TextView = findViewById(R.id.btnAddWish)
        var index: Int = intent.getIntExtra("index", 0)
        var luid = intent.getStringExtra("uid")
        var lprodId = intent.getStringExtra("prodId")

        var database: DatabaseReference = Firebase.database.reference
        val userLog = Firebase.auth.currentUser

        if (index == 1) {
            btn.text = "Удалить"
            btn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.remove_circle), null, null)
        }

        btn.setOnClickListener {
            if (index == 0) {
                index = 1
                listWishes.add(Wish(luid!!, lprodId!!))
                database.child("Users").child(userLog?.uid!!).child("wishes").setValue(listWishes)
                Toast.makeText(this, "Добавлено в желаемое", Toast.LENGTH_SHORT).show()
                database.child("Users").child(userLog?.uid!!).child("ChatMessage").child(luid).
                    child(UUID.randomUUID().toString()).setValue(ChatMessage("", "", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss"))))
                database.child("Users").child(luid).child("ChatMessage").child(userLog?.uid!!).
                    child(UUID.randomUUID().toString()).setValue(ChatMessage("", "", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss"))))
                btn.text = "Удалить"
                btn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.remove_circle), null, null)
            }
            else {
                listWishes.remove(Wish(luid!!, lprodId!!))
                database.child("Users").child(userLog?.uid!!).child("wishes").setValue(listWishes)
                Toast.makeText(this, "Удалено из желаемого", Toast.LENGTH_SHORT).show()
                database.child("Users").child(userLog?.uid!!).child("ChatMessage").child(luid!!).removeValue()
                database.child("Users").child(luid!!).child("ChatMessage").child(userLog?.uid!!).removeValue()
                btn.text = "В избранное"
                btn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.add_circle), null, null)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}