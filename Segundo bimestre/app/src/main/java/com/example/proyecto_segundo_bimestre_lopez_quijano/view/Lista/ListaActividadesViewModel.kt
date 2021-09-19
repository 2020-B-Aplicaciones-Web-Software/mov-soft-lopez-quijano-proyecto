package com.example.proyecto_segundo_bimestre_lopez_quijano.view.Lista

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListaActividadesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is lista_actividades Fragment"
    }
    val text: LiveData<String> = _text
}