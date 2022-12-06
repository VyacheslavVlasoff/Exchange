package com.example.myapplication.ui.wishes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentWishesBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class WishesFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { return inflater.inflate(R.layout.fragment_wishes, container, false) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userLog = Firebase.auth.currentUser
        recyclerView = view.findViewById(R.id.wishesRecycler)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.adapter = CustomRecyclerAdapterForWishes(listWishes)

        database = Firebase.database.reference
    }

}

class CustomRecyclerAdapterForWishes(private val names: List<Wish>) : RecyclerView
.Adapter<CustomRecyclerAdapterForWishes.MyViewHolder>() {

    private var database: DatabaseReference = Firebase.database.reference
    val userLog = Firebase.auth.currentUser

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_home)
        val costText: TextView = itemView.findViewById(R.id.text_cost)
        val image: ImageView = itemView.findViewById(R.id.img)
        val imgHeart: ImageButton = itemView.findViewById(R.id.imgBtnHeart)
        var index: Int = 1
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
        holder.imgHeart.setBackgroundResource(R.drawable.icon_heart_red)
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
                listWishes.add(data)
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
