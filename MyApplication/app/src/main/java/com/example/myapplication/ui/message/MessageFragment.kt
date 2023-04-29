package com.example.myapplication.ui.message

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.ui.home.CustomRecyclerAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MessageFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        val userLog = Firebase.auth.currentUser
        var chatUsers = mutableListOf<String?>()

        database.child("Users").child(userLog?.uid!!).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                snapshot.child("ChatMessage").children.forEach { item ->
                    chatUsers.add(item.key)
                }

                recyclerView = view.findViewById(R.id.chatUser)
                recyclerView.layoutManager = GridLayoutManager(view.context, 1)
                recyclerView.adapter = CustomRecyclerAdapterChat(chatUsers)
            }
        }

    }
}

class CustomRecyclerAdapterChat(private val chatUsers: List<String?>) : RecyclerView
.Adapter<CustomRecyclerAdapterChat.MyViewHolder>() {

    private var database: DatabaseReference = Firebase.database.reference
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.chatUserName)
        val image: ImageView = itemView.findViewById(R.id.chatUserImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_user, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        database.child("Users").child(chatUsers[position]!!).get().addOnCompleteListener {
            val snapshot = it.result
            holder.name.text = snapshot.child("name").getValue(String::class.java)
            Picasso.get().load(snapshot.child("avatar").getValue(String::class.java)).into(holder.image)
        }
        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, ChatActivity::class.java).apply {
                putExtra("user", chatUsers[position])
            })
        }
    }

    override fun getItemCount() = chatUsers.size
}