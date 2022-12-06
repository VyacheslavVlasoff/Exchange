package com.example.myapplication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.home.CustomRecyclerAdapter

class KatalogSearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_katalog_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = kategory
        var products = mutableListOf<Product>()
        for(item in listProduct) {
            if (item.type == kategory) {
                products.add(item)
            }
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.sortKatalog)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        recyclerView.adapter = CustomRecyclerAdapter(listProduct.filter { item -> item.type == kategory})
    }

}