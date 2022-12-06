package com.example.myapplication.ui.katalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KatalogViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val setectedItem: LiveData<String> get() = _text
    fun selectItem(text: String) {
        _text.value = text
    }

    val text: LiveData<String> = _text
}