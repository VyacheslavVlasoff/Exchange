package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.util.*


var sortList = booleanArrayOf(true, true, true)
class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actionbar_fast_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.open_sort -> {
            //findNavController().navigate(R.id.sortFragment)
            val sortFr: SortFragment = SortFragment()
            sortFr.setTargetFragment(this, 1)
            val ft: FragmentTransaction? = fragmentManager?.beginTransaction()
            sortFr.show(ft!!, "myDialog")
            true
        }
        else -> {
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Toast.makeText(view?.context, "${sortList[0]} ${sortList[1]} ${sortList[2]}", Toast.LENGTH_SHORT).show()
        showProductsBySort()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortList = booleanArrayOf(true, true, true)

        val userLog = Firebase.auth.currentUser
        recyclerView = view.findViewById(R.id.spisok)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)

        database = Firebase.database.reference

        listWishes.clear()
        database.child("Users").child(userLog?.uid!!).child("wishes").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                snapshot.children.forEach { item ->
                    listWishes.add(Wish(
                        snapshot.child(item.key!!).child("uid").getValue(String::class.java)!!,
                        snapshot.child(item.key!!).child("prodId").getValue(String::class.java)!!,
                        snapshot.child(item.key!!).child("request").getValue(Boolean::class.java)!!
                    ))
                }
            }
        }

        //список товаров
        var products = mutableListOf<Product>()
        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                products = mutableListOf<Product>()
                val userProducts = mutableListOf<Product>()
                val users = mutableListOf<User>()
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
                            userProducts.add(element)
                            products.add(element)
                        }
                        users.add(
                            User(
                                email.key.toString(),
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
                recyclerView.adapter = CustomRecyclerAdapter(products, listWishes)
            }
        }

        val edittext: EditText = view.findViewById(R.id.searchLine)
        edittext.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString(), products)
            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }

        })

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            //edittext.setText(token)
        })


    }

    @SuppressLint("SuspiciousIndentation")
    fun showProductsBySort() {
        val userLog = Firebase.auth.currentUser
        database = Firebase.database.reference

        //список товаров
        var products = mutableListOf<Product>()
        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                products = mutableListOf<Product>()
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
                            if (element.cost == "Бесплатно" && sortList[0]
                                || element.cost == "Обмен" && sortList[1]
                                || element.cost != "Бесплатно" && element.cost != "Обмен" && sortList[2])
                            products.add(element)
                        }
                    }
                }
                recyclerView.adapter = CustomRecyclerAdapter(products, listWishes)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        showProductsBySort()
    }

    private fun filter(text: String, product: MutableList<Product>) {
        // creating a new array list to filter our data.
        val filteredlist: ArrayList<Product> = ArrayList<Product>()

        // running a for loop to compare elements.
        for (item in product) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.name?.toLowerCase()!!.contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        recyclerView.adapter = CustomRecyclerAdapter(filteredlist, listWishes)
        if (filteredlist.isEmpty() && text != "") {
            view?.findViewById<TextView>(R.id.textProductList)?.text = "Ничего не найдено"
        }
        else {
            view?.findViewById<TextView>(R.id.textProductList)?.text = ""
        }
    }
}

class CustomRecyclerAdapter(private val names: List<Product>, private val wishes: List<Wish>) : RecyclerView
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

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameText.text = names[position].name
        holder.costText.text = names[position].cost
        Picasso.get().load(names[position].image).into(holder.image)
        holder.imgHeart.setBackgroundResource(R.drawable.icon_heart)
        for(wish in wishes) {
            if (wish.uid == names[position].uid && wish.prodId == names[position].prodId)
            {
                holder.imgHeart.setBackgroundResource(R.drawable.icon_heart_red)
            holder.index = 1
            }
        }
        holder.itemView.setOnClickListener { view ->
            view.context.startActivity(Intent(view.context, ProductActivity::class.java).apply {
                putExtra("name", names[position].name)
                putExtra("cost", names[position].cost)
                putExtra("img", names[position].image)
                putExtra("description", names[position].description)
                putExtra("location", names[position].location)
            putExtra("index", holder.index)
            putExtra("uid", names[position].uid)
            putExtra("prodId", names[position].prodId)})
        }
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