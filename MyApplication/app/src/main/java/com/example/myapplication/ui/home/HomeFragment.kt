package com.example.myapplication.ui.home

import android.R
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.gson.Gson


data class User(val Name: String, val Cost: String)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val jsonString = """{"Name":"Кроссовки", "Cost": "12500 руб"}"""
        val gson = Gson()
        val user: User = gson.fromJson(jsonString, User::class.java)
        val textView: TextView = binding.textHome
        val textView2: TextView = binding.textCost
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = user.Name
            textView2.text = user.Cost
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}