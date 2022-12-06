package com.example.myapplication.ui.katalog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.R
import com.google.firebase.database.*


class KatalogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_katalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.katalogRecycler)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = CustomRecyclerAdapterForKatalog(listType)
    }
}

class CustomRecyclerAdapterForKatalog(private val names: List<String>) : RecyclerView
.Adapter<CustomRecyclerAdapterForKatalog.MyKatalogViewHolder>() {

    class MyKatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeText: TextView = itemView.findViewById(R.id.katalogItemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyKatalogViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_katalog, parent, false)
        return MyKatalogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyKatalogViewHolder, position: Int) {
        holder.typeText.text = names[position]
        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(R.id.katalogSearchFragment).apply {
                kategory = names[position]
            }
        }
    }

    override fun getItemCount() = names.size

}