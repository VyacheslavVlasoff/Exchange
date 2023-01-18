package com.example.myapplication.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList


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
            true
        }
        else -> {
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userLog = Firebase.auth.currentUser
        recyclerView = view.findViewById(R.id.spisok)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)

        database = Firebase.database.reference

        /*val elements = mutableListOf<AddProduct>(
            AddProduct("Книга \"Волк с Уолл Стрит\"","Обмен", "Книги",
                "Хочу поменять книгу на первые две книги по Гарри Поттеру", "",
            "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/5a216fb4c6ab9e30b80dadd9.jpg?alt=media&token=a3a70c6a-166f-415c-a658-737762f4da03"),
            AddProduct("Тетрадь в клетку","Бесплатно", "Для учебы",
                "Отдаю за ненадобностью. Совсем новая", "",
            "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/879f76207652292a70b07955ce5445f8.jpg?alt=media&token=fa8cc6d2-2554-42a8-97e9-3001e8558dac"),
            AddProduct("Одежда на девочку","Бесплатно", "Для детей", "", "",
            "https://firebasestorage.googleapis.com/v0/b/my-application-f8aff.appspot.com/o/tyomka37-product-images-3409.jpg?alt=media&token=edd039a8-da17-43e3-82bb-10ba88310930")
        )
        database.child("Users").child(userLog?.uid!!).child("products").setValue(elements)*/

        //список желаний
        val wishes = mutableListOf<Wish>()
        database.child("Users").child(userLog?.uid!!).child("wishes").get().addOnCompleteListener { wish ->
            if (wish.isSuccessful) {
                val snapshot2 = wish.result
                snapshot2.children.forEach { pr ->
                    wishes.add(Wish(
                        userLog.uid.toString(),
                        pr.key!!.toInt(),
                        snapshot2.child(pr.key!!).child("request").getValue(Boolean::class.java)!!
                    )
                    )
                }
            }
        }

        for(wish in wishes) {
            listWishes.add(wish)
        }

        //список товаров
        var products = mutableListOf<Product>()
        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                products = mutableListOf<Product>()
                //listProduct.clear()
                val userProducts = mutableListOf<Product>()
                val users = mutableListOf<User>()
                //var wishes = mutableListOf<Wish>()
                snapshot.children.forEach { email ->
                    if (email.key != userLog?.uid) {
                        var snapshot2 = snapshot.child(email.key!!).child("products")
                        snapshot2.children.forEach { pr ->
                            val element = Product(
                                email.key.toString(),
                                pr.key!!.toInt(),
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
        /*edittext.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                fun View.hideKeyboard() {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0)
                }
                edittext.hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })*/
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