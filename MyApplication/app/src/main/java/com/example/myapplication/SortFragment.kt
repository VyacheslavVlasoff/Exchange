package com.example.myapplication

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.home.HomeFragment
import com.example.myapplication.ui.home.sortList

class SortFragment : DialogFragment() {

    private lateinit var switch1: Switch
    private lateinit var switch2: Switch
    private lateinit var switch3: Switch
    private lateinit var btn: Button

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
        switch1.isChecked = sortList[0]
        switch2.isChecked = sortList[1]
        switch3.isChecked = sortList[2]

        switch1.setOnClickListener {
            if (!switch2.isChecked && !switch3.isChecked) switch1.isChecked = true
        }

        switch2.setOnClickListener {
            if (!switch1.isChecked && !switch3.isChecked) switch2.isChecked = true
        }

        switch3.setOnClickListener {
            if (!switch1.isChecked && !switch2.isChecked) switch3.isChecked = true
        }

        btn = view.findViewById(R.id.setSortBtn)
        btn.setOnClickListener {
            sortList = booleanArrayOf(switch1.isChecked, switch2.isChecked, switch3.isChecked)
            targetFragment?.onActivityResult(1, Activity.RESULT_OK, Intent())
            dismiss()
        }
    }
}