package com.example.proyecto_segundo_bimestre_lopez_quijano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
            Log.i("asd", "Usuario: ${it}")
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
                agregarMiembro(lista)
            }
        }

        // Actualizar cambios
        val botonActualizar = findViewById<Button>(R.id.btn_actualizarCambiosLista)
        botonActualizar.setOnClickListener {
            if (txtTitulo.text.isEmpty()) {
                val msg = Toast.makeText(this, "Ingrese un título válido", Toast.LENGTH_SHORT)
                msg.show()
            } else {
                actualizarCambios(txtTitulo.text.toString())
            }
        }

    }

    fun actualizarCambios(titulo: String) {


        // TODO: UPDATE
    }

    fun agregarMiembro(lista: Lista) {
        val builder = AlertDialog.Builder(this)
        val txtCorreo = EditText(this)
        txtCorreo.hint = "ejemplo@mail.com"
        txtCorreo.background = resources.getDrawable(R.drawable.et_borders)
        val scale = resources.displayMetrics.density
        txtCorreo.width = (10 * scale + 0.5f).toInt()
        txtCorreo.height = (48 * scale + 0.5f).toInt()
        txtCorreo.setPadding(100, txtCorreo.paddingTop, 100, txtCorreo.paddingBottom)
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
                    //val nuevoUsuario = Usuario()
                    //lista.usuarios?.add(nuevoUsuario)
                    dialog.dismiss()    // Cuando ya se registre
                }
            }

    }

}