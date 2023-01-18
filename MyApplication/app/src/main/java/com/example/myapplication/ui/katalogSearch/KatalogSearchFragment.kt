package com.example.myapplication.ui.katalogSearch

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class KatalogSearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_katalog_search, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actionbar_fast_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.open_sort -> {
            true
        }
        else -> {
            super.onOptionsItemSelected(item);
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = kategory
        var products = mutableListOf<Product>()
        val ind = mutableListOf<Int>()
        for(item in listProduct) {
            if (item.type == kategory) {
                products.add(item)
                ind.add(indexH[listProduct.indexOf(item)])
            }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.sortKatalog)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.adapter = CustomRecyclerAdapterForElement(products, ind)
    }
}

class CustomRecyclerAdapterForElement(private val names: List<Product>, private val ind: List<Int>) : RecyclerView
.Adapter<CustomRecyclerAdapterForElement.MyViewHolder>() {

    private var database: DatabaseReference = Firebase.database.reference
    val userLog = Firebase.auth.currentUser

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_home)
        val costText: TextView = itemView.findViewById(R.id.text_cost)
        val image: ImageView = itemView.findViewById(R.id.img)
        val imgHeart: ImageButton = itemView.findViewById(R.id.imgBtnHeart)
        var index: Int = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameText.text = names[position].name
        holder.costText.text = names[position].cost
        Picasso.get().load(names[position].image).into(holder.image)
        var data = Wish(names[position].name, names[position].cost, names[position].type, names[position].description,
            names[position].location, names[position].image, false)
        holder.index = ind[position]
        if (holder.index == 1) {
            holder.imgHeart.setBackgroundResource(R.drawable.icon_heart_red)
        }
        else holder.imgHeart.setBackgroundResource(R.drawable.icon_heart)
        holder.itemView.setOnClickListener { view ->
            view.context.startActivity(Intent(view.context, ProductActivity::class.java).apply {
                putExtra("name", names[position].name)
                putExtra("cost", names[position].cost)
                putExtra("img", names[position].image)
                putExtra("description", names[position].description)
                putExtra("location", names[position].location)}) }
        holder.imgHeart.setOnClickListener {
            if (holder.index == 0) {
                holder.imgHeart.setBackgroundResource(R.drawable.icon_heart_red)
                holder.index = 1
                var point = true
                for (wish in listWishes)
                    if (wish == data) point = false
                if (point) listWishes.add(data)
                database.child("Users").child(userLog?.uid!!).child("wishes").setValue(listWishes)
                Toast.makeText(holder.itemView.context, "Добавлено в желаемое", Toast.LENGTH_SHORT).show()
            }
            else {
                holder.imgHeart.setBackgroundResource(R.drawable.icon_heart)
                holder.index = 0
                listWishes.remove(data)
                database.child("Users").child(userLog?.uid!!).child("wishes").setValue(listWishes)
                Toast.makeText(holder.itemView.context, "Удалено из желаемого", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = names.size
}