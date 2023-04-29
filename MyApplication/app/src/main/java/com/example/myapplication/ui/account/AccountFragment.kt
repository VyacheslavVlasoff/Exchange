package com.example.myapplication.ui.account

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.ui.home.CustomRecyclerAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException


class AccountFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var imgAvatar: ImageView

    private var mCropImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.actionbar_account, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.navigation_to_settings -> {
            startActivity(Intent(context, SettingsActivity::class.java))
            true
        }
        else -> {
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnAdd: ImageButton = view.findViewById(R.id.imageButtonAdd)
        btnAdd.setOnClickListener {
            startActivity(Intent(view.context, CreateProductActivity::class.java))
        }

        val userLog = Firebase.auth.currentUser
        database = Firebase.database.reference
        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                snapshot.children.forEach { id ->
                if (id.key == userLog?.uid) {
                    val avatar: CircleImageView = view.findViewById(R.id.profile_image)
                    val nameUser: TextView = view.findViewById(R.id.textViewNameUser)
                    val surnameUser: TextView = view.findViewById(R.id.textViewSurnameUser)
                    val phoneUser: TextView = view.findViewById(R.id.textViewPhoneUser)
                    val emailUser: TextView = view.findViewById(R.id.textViewMailUser)
                    Picasso.get().load(snapshot.child(userLog?.uid!!).child("avatar").getValue(String::class.java)).into(avatar)
                    nameUser.text = snapshot.child(userLog?.uid!!).child("name").getValue(String::class.java)
                    surnameUser.text = snapshot.child(userLog?.uid!!).child("surname").getValue(String::class.java)
                    phoneUser.text = snapshot.child(userLog?.uid!!).child("phone").getValue(String::class.java)
                    emailUser.text = userLog?.email
                }
                }
            }
        }

        database.child("Users").get().addOnCompleteListener { user ->
            if (user.isSuccessful) {
                val snapshot = user.result
                val products = mutableListOf<Product>()
                //val userProducts = mutableListOf<Product>()
                //val users = mutableListOf<User>()
                snapshot.children.forEach { email ->
                    if (email.key == userLog?.uid) {
                        val snapshot2 = snapshot.child(email.key!!).child("products")
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
                            //userProducts.add(element)
                            products.add(element)
                        }
                        /*users.add(
                            User(
                                snapshot.child(email.key!!).child("name").getValue(String::class.java),
                                snapshot.child(email.key!!).child("surname").getValue(String::class.java),
                                snapshot.child(email.key!!).child("phone").getValue(String::class.java),
                                userProducts,
                                snapshot.child(email.key!!).child("image").getValue(String::class.java)
                            )
                        )

                         */
                        //userProducts.removeAll(products)
                    }
                }
                recyclerView = view.findViewById(R.id.spisokUser)
                recyclerView.layoutManager = GridLayoutManager(view.context, 2)
                recyclerView.adapter = CustomRecyclerAdapterForUser(products)
            }
        }

        imgAvatar = view.findViewById(R.id.profile_image)
        imgAvatar.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1 -> if (resultCode === AppCompatActivity.RESULT_OK) {
                var bitmap: Bitmap = Bitmap.createBitmap(100, 100,
                    Bitmap.Config.ARGB_8888);
                val imageView: ImageView = view!!.findViewById(R.id.profile_image)

                val selectedImage: Uri? = data?.data
                mCropImageUri = selectedImage
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImage)
                    val width = bitmap.width
                    val height = bitmap.height
                    val newWidth = if (height > width) width else height
                    val newHeight =
                        if (height > width) height - (height - width) else height
                    var cropW = (width - height) / 2
                    cropW = if (cropW < 0) 0 else cropW
                    var cropH = (height - width) / 2
                    cropH = if (cropH < 0) 0 else cropH

                    bitmap = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                imageView.setImageBitmap(bitmap)
            }
            100 -> if (resultCode == AppCompatActivity.RESULT_OK) {
                var cropIntent = Intent("com.android.camera.action.CROP")
                // indicate image type and Uri
                cropIntent.setDataAndType(data?.data, "image/*")
                // set crop properties here
                cropIntent.putExtra("crop", true)
                // indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1)
                cropIntent.putExtra("aspectY", 1)
                // indicate output X and Y
                cropIntent.putExtra("outputX", 128)
                cropIntent.putExtra("outputY", 128)
                // retrieve data on return
                cropIntent.putExtra("return-data", true)
                // start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, 1)
            }
        }
    }
}

class CustomRecyclerAdapterForUser(private val names: List<Product>) : RecyclerView
.Adapter<CustomRecyclerAdapterForUser.MViewHolder>() {

    class MViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_home)
        val costText: TextView = itemView.findViewById(R.id.text_cost)
        val image: ImageView = itemView.findViewById(R.id.img)
        val imgHeart: ImageButton = itemView.findViewById(R.id.imgBtnHeart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return MViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.nameText.text = names[position].name
        holder.costText.text = names[position].cost
        Picasso.get().load(names[position].image).into(holder.image)
        holder.imgHeart.isClickable = false
        holder.imgHeart.isVisible = false
        holder.itemView.setOnClickListener { view ->
            view.context.startActivity(Intent(view.context, ProductUserActivity::class.java).apply {
                putExtra("name", names[position].name)
                putExtra("cost", names[position].cost)
                putExtra("img", names[position].image)
                putExtra("description", names[position].description)
                putExtra("location", names[position].location)
            putExtra("uid", names[position].prodId)
            putExtra("uidUser", names[position].uid)}) }
    }

    override fun getItemCount() = names.size
}