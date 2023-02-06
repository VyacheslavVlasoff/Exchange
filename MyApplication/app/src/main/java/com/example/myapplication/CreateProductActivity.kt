package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class CreateProductActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        database = Firebase.database.reference

        val btnCreateProduct: Button = findViewById(R.id.btnAddProduct)
        val nameProduct: EditText = findViewById(R.id.etNameProduct)
        val type: Spinner = findViewById(R.id.spinnerTypes)
        val costProduct: EditText = findViewById(R.id.etCostProduct)
        val typeProduct: Spinner = findViewById(R.id.spinnerTypesProduct)
        val location: AutoCompleteTextView = findViewById(R.id.etLocationProduct)
        val description: EditText = findViewById(R.id.etDescriptionProduct)

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            this, R.array.types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        type.adapter = adapter

        var streets = mutableListOf<String>()
        database.child("Streets").get().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                snapshot.children.forEach { street ->
                    snapshot.child(street.key!!).children.forEach {num->
                        streets.add(street.key + ", " +
                            snapshot.child(street.key!!).child(num.key!!).getValue(String::class.java)!!)
                    }
                    //streets.add(snapshot.child(street.key!!).getValue(String::class.java)!!)
                }
            }
        }

        val locationAdapter: ArrayAdapter<*> = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line, streets)
        location.setAdapter(locationAdapter)
        //location.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        val itemSelectedListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    // Получаем выбранный объект
                    costProduct.isEnabled = type.selectedItem == "Продажа"
                    costProduct.text.clear()
                    costProduct.text.append('0')
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        type.onItemSelectedListener = itemSelectedListener

        costProduct.isEnabled = type.selectedItem == "Продажа"

        btnCreateProduct.setOnClickListener {
            Toast.makeText(this, "${nameProduct.text}\n${type.selectedItem}\n${costProduct.text}\n${typeProduct.selectedItem}\n${location.text}\n${description.text}", Toast.LENGTH_SHORT).show()
        }

    }
}