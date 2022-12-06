package com.example.myapplication.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.nio.InvalidMarkException


class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userLog = Firebase.auth.currentUser
        recyclerView = view.findViewById(R.id.spisok)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)

        database = Firebase.database.reference

        /*    val products = mutableListOf(
            Product("Кроссовки1", "1250", "Обувь", "", ""),
            Product("Детская одежда1", "Бесплатно", "Одежда", "", ""),
            Product("Тетрадь в клетку1", "Обмен", "Другое", "", ""),
            Product("Спининг1", "7200", "Другое", "", ""),
            Product("Сковородка1", "Бесплатно", "Посуда", "", ""),
            Product("Набор резинок1", "Обмен", "Другое", "", ""),
            Product("\"Волк с Уолл-Стрит\"1", "Бесплатно", "Другое", "", "")
        )
        database.child("Users").child("New1").setValue(User("","" , "", products, ""))*/

        database.child("Users").child(userLog?.uid!!).child("wishes").get().addOnCompleteListener { wish ->
            if (wish.isSuccessful) {

                val snapshot2 = wish.result
                snapshot2.children.forEach { pr ->
                    val wish = Wish(
                        snapshot2.child(pr.key!!).child("name").getValue(String::class.java),
                        snapshot2.child(pr.key!!).child("cost").getValue(String::class.java),
                        snapshot2.child(pr.key!!).child("type").getValue(String::class.java),
                        snapshot2.child(pr.key!!).child("description").getValue(String::class.java),
                        snapshot2.child(pr.key!!).child("location").getValue(String::class.java),
                        snapshot2.child(pr.key!!).child("image").getValue(String::class.java),
                        snapshot2.child(pr.key!!).child("request").getValue(Boolean::class.java)
                    )
                    if (listWishes.size == 0) listWishes.add(wish)
                    else {
                        var point = true
                        for (wishes in listWishes)
                            if (wishes == wish) point = false
                        if (point) listWishes.add(wish)
                    }
                }
            }
        }

        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                val products = mutableListOf<Product>()
                listProduct.clear()
                val userProducts = mutableListOf<Product>()
                val users = mutableListOf<User>()
                var wishes = mutableListOf<Wish>()
                snapshot.children.forEach { email ->
                    if (email.key != userLog?.uid) {
                        var snapshot2 = snapshot.child(email.key!!).child("products")
                        snapshot2.children.forEach { pr ->
                            val element = Product(
                                snapshot2.child(pr.key!!).child("name").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("cost").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("type").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("description").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("location").getValue(String::class.java),
                                snapshot2.child(pr.key!!).child("image").getValue(String::class.java)
                            )
                            userProducts.add(element)
                            products.add(element)
                            listProduct.add(element)
                            indexH.add(products.indexOf(element),0);
                            for (wish in listWishes)
                                if (Wish(element.name, element.cost, element.type, element.description, element.location,
                                    element.image, false) == wish) indexH.add(products.indexOf(element), 1)
                        }
                        users.add(
                            User(
                                snapshot.child(email.key!!).child("name").getValue(String::class.java),
                                snapshot.child(email.key!!).child("surname").getValue(String::class.java),
                                snapshot.child(email.key!!).child("phone").getValue(String::class.java),
                                userProducts,
                                snapshot.child(email.key!!).child("image").getValue(String::class.java)
                            )
                        )
                        userProducts.removeAll(products)
                    }
                }
                recyclerView.adapter = CustomRecyclerAdapter(products)
            }
        }

        val edittext: EditText = view.findViewById(R.id.searchLine)
        edittext.setOnKeyListener (View.OnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) and
                (i == KeyEvent.KEYCODE_ENTER)) {
                Toast.makeText(view.context, "Enter", Toast.LENGTH_SHORT).show()
                fun View.hideKeyboard() {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0)
                }
                edittext.hideKeyboard()
                true
            }
            false
        })

        //recyclerView.setOnClickListener { startActivity(Intent(view.context, ProductActivity::class.java)) }

        /*
        database.child("Users").child("New").child("products").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val products = mutableListOf(Product("", "", "", "", ""))
                snapshot.children.forEach { pr ->
                    products.add(Product(snapshot.child(pr.key!!).child("name").getValue(String::class.java),
                        snapshot.child(pr.key!!).child("cost").getValue(String::class.java),
                        snapshot.child(pr.key!!).child("type").getValue(String::class.java),
                        snapshot.child(pr.key!!).child("description").getValue(String::class.java),
                        snapshot.child(pr.key!!).child("location").getValue(String::class.java))
                    )
                }
                products.removeAt(0)
                val recyclerView: RecyclerView = view.findViewById(R.id.spisok)
                recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                recyclerView.adapter = CustomRecyclerAdapter(products)

                //val adapter = MyAdapter(view.context, products)
                //val adapter = ArrayAdapter(view.context, android.R.layout.simple_expandable_list_item_1, products)
                //listView.adapter = adapter
            }
        }

         */

    }



    fun addProduct(productId: String, name: String, cost: String) {
        val prod = Product(name, cost, "", "", "")

        database.child(productId).setValue(prod)
    }
}

class CustomRecyclerAdapter(private val names: List<Product>) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

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
        holder.index = indexH[position]
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

//Class MyAdapter
/*
class MyAdapter(private val context: Context, private val arrayList: MutableList<Product>) : BaseAdapter() {
    //private lateinit var img: ImageView
    private lateinit var name: TextView
    private lateinit var cost: TextView
    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        //img = convertView.findViewById(R.id.serialNumber)
        name = convertView!!.findViewById(R.id.text_home)
        cost = convertView!!.findViewById(R.id.text_cost)
        //serialNum.text = " " + arrayList[position].num
        name.text = arrayList[position].name
        cost.text = arrayList[position].cost
        return convertView
    }
}

 */