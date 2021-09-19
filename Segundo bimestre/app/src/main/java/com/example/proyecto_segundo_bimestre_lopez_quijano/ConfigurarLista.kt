package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista

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
        val builder = AlertDialog.Builder(this)
        val txtCorreo = EditText(this)
        txtCorreo.hint = "ejemplo@mail.com"
        txtCorreo.background = resources.getDrawable(R.drawable.et_borders)
        val scale = resources.displayMetrics.density
        txtCorreo.width = (200 * scale + 0.5f).toInt()
        txtCorreo.height = (48 * scale + 0.5f).toInt()
        //txtCorreo.setp
        //txtCorreo.marginTop = 28
        txtCorreo.setEms(10)

        builder.setTitle("Añadir miembro")
        builder.setMessage("Correo electrónico:")
        builder.setView(txtCorreo)
        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (txtCorreo.text.isEmpty()) {
                    val msg = Toast.makeText(this, "Llene el campo de correo", Toast.LENGTH_SHORT)
                    msg.show()
                } else {
                    Log.i("asd", "Agregar miembro ${txtCorreo.text}")
                    // TODO: si el usuario existe, entonces decir que se hico exitosamente, caso contrario NO
                }
            }

    }

}