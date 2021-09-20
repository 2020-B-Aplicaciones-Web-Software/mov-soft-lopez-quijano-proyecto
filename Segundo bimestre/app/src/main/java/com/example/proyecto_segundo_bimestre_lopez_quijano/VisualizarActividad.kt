package com.example.proyecto_segundo_bimestre_lopez_quijano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Actividad
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class VisualizarActividad : AppCompatActivity() {

    // Referencia Firestore
    val db = Firebase.firestore

    // Listas
    val listaPrioridades: MutableList<String> = ArrayList()
    val listaEtiquetas: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_actividad)

        // Recibir intent
        val actividad = intent.getParcelableExtra<Actividad>("actividad")
        val etiquetas = intent.getParcelableArrayListExtra<Etiqueta>("etiquetas")

        // Campos
        val txtTitulo = findViewById<EditText>(R.id.et_tituloActualizar)
        val txtFechaCreacion = findViewById<TextView>(R.id.tv_fechaVisualizar)
        val txtUsuarioCreador = findViewById<TextView>(R.id.tv_usuarioVisualizar)
        val txtDescripcion = findViewById<TextView>(R.id.et_descripcionVisualizar)
        val spinnerPrioridad = findViewById<Spinner>(R.id.sp_prioridadVisualizar)
        val txtFechaVencimiento = findViewById<EditText>(R.id.et_fechaVisualizar)
        val spinnerEtiqueta = findViewById<Spinner>(R.id.sp_etiquetaVisualizar)

        // Rellenar los campos
        txtTitulo.setText(actividad?.titulo)
        val format = SimpleDateFormat("dd/MM/yyyy")
        txtFechaCreacion.setText(format.format(actividad?.fechaCreacion))
        txtUsuarioCreador.setText(actividad?.usuarioCreador.toString())
        txtDescripcion.setText(actividad?.descripcion)
        txtFechaVencimiento.setText(format.format(actividad?.fechaVencimiento))

        obtenerPrioridades(spinnerPrioridad)
        spinnerPrioridad.setSelection(actividad?.prioridad!! - 1)

        obtenerEtiquetas(etiquetas!!)
        spinnerEtiqueta.setSelection(listaEtiquetas.indexOf(actividad.etiqueta.toString()))

    }

    fun obtenerPrioridades(spinner: Spinner){
        for (i in 1..5){
            listaPrioridades.add(i.toString())
        }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, listaPrioridades
        )
        spinner.adapter = adapter
    }

    fun obtenerEtiquetas(etiqueta: java.util.ArrayList<Etiqueta>){
        for (nombre in etiqueta){
            listaEtiquetas.add(nombre.toString())
        }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, listaEtiquetas
        )
        val spinner = findViewById<Spinner>(R.id.sp_etiquetaVisualizar)
        spinner.adapter = adapter
    }

    override fun onStop() {
        //TODO: Probar con el onDestroy si este no vale
        //TODO: teoricamente cuando nos salimos se activa el de destruir pero antes se pausa

        // Campos
        val txtTitulo = findViewById<EditText>(R.id.et_tituloActualizar)
        val txtFechaCreacion = findViewById<TextView>(R.id.tv_fechaVisualizar)
        val txtUsuarioCreador = findViewById<TextView>(R.id.tv_usuarioVisualizar)
        val txtDescripcion = findViewById<TextView>(R.id.et_descripcionVisualizar)
        val spinnerPrioridad = findViewById<Spinner>(R.id.sp_prioridadVisualizar)
        val txtFechaVencimiento = findViewById<EditText>(R.id.et_fechaVisualizar)
        val spinnerEtiqueta = findViewById<Spinner>(R.id.sp_etiquetaVisualizar)



        super.onStop()
    }
}