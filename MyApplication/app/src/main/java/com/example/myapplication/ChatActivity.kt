package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.message.CustomRecyclerAdapterChat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getStringExtra("user")!!
        database = Firebase.database.reference
        val userLog = Firebase.auth.currentUser

        supportActionBar?.setBackgroundDrawable(resources.getDrawable(R.color.gradColor1))
        //supportActionBar?.title = "Сообщения"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        database.child("Users").child(user).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                supportActionBar?.title = snapshot.child("name").getValue(String::class.java)
            }
        }

        messageList(user)

        editText = findViewById(R.id.etMessageUser)
        editText.setOnKeyListener (View.OnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) and
                (i == KeyEvent.KEYCODE_ENTER)) {
                //Добавить сообщение обоим пользователям
                val newMessage = ChatMessage(
                    editText.text.toString(),
                    userLog?.uid,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss"))
                )
                database.child("Users").child(userLog?.uid!!).child("ChatMessage")
                    .child(user).child(UUID.randomUUID().toString()).setValue(newMessage)
                database.child("Users").child(user).child("ChatMessage")
                    .child(userLog?.uid!!).child(UUID.randomUUID().toString()).setValue(newMessage)
                editText.setText("")
                messageList(user)
                true
            }
            false
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun messageList(user: String?) {
        database = Firebase.database.reference
        val userLog = Firebase.auth.currentUser

        var chatMessage = mutableListOf<ChatMessage>()
        database.child("Users").child(userLog?.uid!!).child("ChatMessage").child(user!!).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val snapshot = it.result
                snapshot.children.forEach { item ->
                    val mess = snapshot.child(item.key!!).child("text").getValue(String::class.java)
                    val user = snapshot.child(item.key!!).child("user").getValue(String::class.java)
                    if (mess != "" && user != "") {
                        chatMessage.add(
                            ChatMessage(
                                snapshot.child(item.key!!).child("text")
                                    .getValue(String::class.java),
                                snapshot.child(item.key!!).child("user")
                                    .getValue(String::class.java),
                                snapshot.child(item.key!!).child("time")
                                    .getValue(String::class.java)!!
                            )
                        )
                    }
                }
                recyclerView = findViewById(R.id.messageUser)
                recyclerView.layoutManager = GridLayoutManager(this, 1)
                recyclerView.adapter = CustomRecyclerAdapterChatMessage(chatMessage.sortedBy { it.time })
            }
        }
    }
}


class CustomRecyclerAdapterChatMessage(private val chatMessage: List<ChatMessage>) : RecyclerView
.Adapter<CustomRecyclerAdapterChatMessage.MyViewHolder>() {

    private var database: DatabaseReference = Firebase.database.reference
    private val userLog = Firebase.auth.currentUser

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mess: TextView = itemView.findViewById(R.id.messageUserText)
        val image: ImageView = itemView.findViewById(R.id.messageUserImage)
        val timeMess: TextView = itemView.findViewById(R.id.messageTime)
        val boxMess: LinearLayout = itemView.findViewById(R.id.boxMess)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_message, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mess.text = chatMessage[position].text
        holder.timeMess.text = chatMessage[position].time
        if (chatMessage[position].user == userLog?.uid)  {
            holder.boxMess.setBackgroundColor(Color.rgb(223, 255, 255))
        }
        database.child("Users").child(chatMessage[position].user!!).get().addOnCompleteListener {
            val snapshot = it.result
            Picasso.get().load(snapshot.child("avatar").getValue(String::class.java)).into(holder.image)
        }
    }

    override fun getItemCount() = chatMessage.size
}