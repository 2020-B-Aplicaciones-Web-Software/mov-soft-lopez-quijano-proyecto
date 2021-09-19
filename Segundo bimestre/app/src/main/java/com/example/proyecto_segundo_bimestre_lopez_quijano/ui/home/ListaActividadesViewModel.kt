package com.example.proyecto_segundo_bimestre_lopez_quijano.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListaActividadesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragmentdsswrtygkhjgffgh"
    }
    val text: LiveData<String> = _text
}