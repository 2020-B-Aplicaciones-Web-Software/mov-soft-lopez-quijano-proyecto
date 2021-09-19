package com.example.proyecto_segundo_bimestre_lopez_quijano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario

class ConfigurarLista : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_lista)

        val lista = intent.getParcelableExtra<Lista>("lista")

        // Campos
        val txtTitulo = findViewById<EditText>(R.id.et_tituloConfigurarLista)
        val listViewMiembros = findViewById<ListView>(R.id.lv_miembrosLista)

        // Rellenar campos
        txtTitulo.setText(lista?.nombre)

        val datosListView: MutableList<String> = ArrayList<String>()
        lista!!.usuarios!!.forEach {
            datosListView.add(it.toString())
        }
        datosListView.add("+ Agregar miembro")  // Opcion para agregar miembros

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            datosListView
        )
        listViewMiembros.adapter = adapter


        // Agregar miembros
        listViewMiembros.setOnItemClickListener { adapterView, view, position, l ->
            if (position == datosListView.size - 1) {
                agregarMiembro()
            }
        }

    }

    fun agregarMiembro() {
        Log.i("asd", "Agregar Miembro !")
    }

}