package com.example.myapplication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.DialogFragment

class SortFragment : DialogFragment() {

    private lateinit var switch1: Switch
    private lateinit var switch2: Switch
    private lateinit var switch3: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switch1 = view.findViewById(R.id.switch1)
        switch2 = view.findViewById(R.id.switch2)
        switch3 = view.findViewById(R.id.switch3)


    }
}