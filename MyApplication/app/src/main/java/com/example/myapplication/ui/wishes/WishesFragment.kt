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
import com.example.myapplication.ui.home.CustomRecyclerAdapter
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

        database = Firebase.database.reference
        val userLog = Firebase.auth.currentUser
        val textWishes: TextView = view.findViewById(R.id.textWishes)
        if (listWishes.size == 0) textWishes.text = "Список желаний пуст"
        else textWishes.text = ""
        recyclerView = view.findViewById(R.id.wishesRecycler)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)

        //найти список продуктов, которые в списке желаний
        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                val products = mutableListOf<Product>()
                snapshot.children.forEach { email ->
                    if (email.key != userLog?.uid) {
                        var snapshot2 = snapshot.child(email.key!!).child("products")
                        snapshot2.children.forEach { pr ->
                            val element = Product(
                                email.key.toString(),
                                pr.key!!.toString(),
                                snapshot2.child(pr.key!!).child("name").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("cost").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("type").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("description").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("location").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("image").getValue(String::class.java)
                            )
                            for (wish in listWishes) {
                                if (wish.uid == element.uid && wish.prodId == element.prodId)
                                {
                                    products.add(element)
                                }
                            }
                        }
                    }
                }
                recyclerView.adapter = CustomRecyclerAdapterForWishes(products)
            }
        }
    }

}

class CustomRecyclerAdapterForWishes(private val names: List<Product>) : RecyclerView
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
                listWishes.add(Wish(names[position].uid, names[position].prodId))
                database.child("Users").child(userLog?.uid!!).child("wishes").setValue(listWishes)
                Toast.makeText(holder.itemView.context, "Добавлено в желаемое", Toast.LENGTH_SHORT).show()
            }
            else {
                holder.imgHeart.setBackgroundResource(R.drawable.icon_heart)
                holder.index = 0
                listWishes.remove(Wish(names[position].uid, names[position].prodId))
                database.child("Users").child(userLog?.uid!!).child("wishes").setValue(listWishes)
                Toast.makeText(holder.itemView.context, "Удалено из желаемого", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = names.size

}
