package com.example.proyecto_segundo_bimestre_lopez_quijano.view.Actividad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_segundo_bimestre_lopez_quijano.R
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Actividad
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.view.Lista.ListaDeActividades
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class VisualizarActividad : AppCompatActivity() {

    // Referencia Firestore
    val db = Firebase.firestore

    // Listas
    val listaPrioridades: MutableList<String> = ArrayList()
    val listaEtiquetas: MutableList<String> = ArrayList()

    // Intent Explicito
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401
    lateinit var lista: Lista
    lateinit var actividad: Actividad

    // Campos
    lateinit var txtTitulo: EditText
    lateinit var txtDescripcion: EditText
    lateinit var spinnerPrioridad: Spinner
    lateinit var txtFechaVencimiento: EditText
    lateinit var spinnerEtiqueta: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_actividad)

        // Recibir intent
        actividad = intent.getParcelableExtra("actividad")!!
        lista = intent.getParcelableExtra("lista")!!
        this.setTitle(lista.nombre)

        // Campos
        txtTitulo = findViewById(R.id.et_tituloActualizar)
        val txtFechaCreacion = findViewById<TextView>(R.id.tv_fechaVisualizar)
        val txtUsuarioCreador = findViewById<TextView>(R.id.tv_usuarioVisualizar)
        txtDescripcion = findViewById(R.id.et_descripcionVisualizar)
        spinnerPrioridad = findViewById(R.id.sp_prioridadVisualizar)
        txtFechaVencimiento = findViewById(R.id.et_fechaVisualizar)
        spinnerEtiqueta = findViewById(R.id.sp_etiquetaVisualizar)

        // Rellenar los campos
        txtTitulo.setText(actividad.titulo)
        val format = SimpleDateFormat("dd/MM/yyyy")
        txtFechaCreacion.setText(format.format(actividad.fechaCreacion))
        txtUsuarioCreador.setText(actividad.usuarioCreador.toString())
        txtDescripcion.setText(actividad.descripcion)
        txtFechaVencimiento.setText(format.format(actividad.fechaVencimiento))

        obtenerPrioridades(spinnerPrioridad)
        spinnerPrioridad.setSelection(actividad.prioridad - 1)

        obtenerEtiquetas(lista.etiquetas!!)
        spinnerEtiqueta.setSelection(listaEtiquetas.indexOf(actividad.etiqueta.toString()))

        spinnerEtiqueta.setOnItemSelectedListener( object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == listaEtiquetas.size - 1) {
                    mostrarDialogoNuevaEtiqueta(lista)
                    obtenerEtiquetas(lista.etiquetas!!)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

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

    fun obtenerEtiquetas(etiqueta: ArrayList<Etiqueta>){
        listaEtiquetas.clear()
        listaEtiquetas.add("[Sin etiqueta]")
        for (nombre in etiqueta){
            listaEtiquetas.add(nombre.toString())
        }
        listaEtiquetas.add("+ Añadir etiqueta")

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, listaEtiquetas
        )
        spinnerEtiqueta.adapter = adapter
    }

    fun actualizarCambios(listaID: String, actividadID: String) {
        val formatoFecha = SimpleDateFormat("dd/M/yyyy")
        val fechaVencimiento = formatoFecha.parse(txtFechaVencimiento.text.toString())

        db.runTransaction { transaction ->
            transaction.update(
                db.collection("Lista/${listaID}/Actividad").document(actividadID),
                mapOf(
                    "titulo" to txtTitulo.text.toString(),
                    "descripcion" to txtDescripcion.text.toString(),
                    "prioridad" to spinnerPrioridad.selectedItem.toString().toInt(),
                    "fecha_vencimiento" to Timestamp(fechaVencimiento),
                    "etiqueta" to spinnerEtiqueta.selectedItem.toString()
                )
            )
        }
            // Volver a la Lista de actividades correspondiente
            .addOnSuccessListener {
                val intentExplicito = Intent(this, ListaDeActividades::class.java)
                intentExplicito.putExtra("listaSeleccionada", lista)
                startActivityForResult(intentExplicito, CODIGO_RESPUESTA_INTENT_EXPLICITO)
            }
    }

    override fun onBackPressed() {
        actualizarCambios(lista.id!!, actividad.id!!)
    }

    fun mostrarDialogoNuevaEtiqueta(lista: Lista?) {
        val builder = AlertDialog.Builder(this)
        val txtEtiqueta = EditText(this)
        txtEtiqueta.hint = "Nueva etiqueta"
        txtEtiqueta.background = resources.getDrawable(R.drawable.et_borders)
        val scale = resources.displayMetrics.density
        txtEtiqueta.width = (10 * scale + 0.5f).toInt()
        txtEtiqueta.height = (48 * scale + 0.5f).toInt()
        txtEtiqueta.setPadding(100, txtEtiqueta.paddingTop, 100, txtEtiqueta.paddingBottom)
        txtEtiqueta.setEms(10)

        builder.setTitle("Crear Etiqueta")
        builder.setMessage("Nombre:")
        builder.setView(txtEtiqueta)
        builder
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()

        // Aceptar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                when {
                    // Campo vacio
                    txtEtiqueta.text.isEmpty() -> {
                        val msg = Toast.makeText(this, "Llene el campo de etiqueta", Toast.LENGTH_SHORT)
                        msg.show()
                    }
                    // Etiqueta que ya esta en la lista
                    lista?.etiquetas!!.map { it.nombre }.contains(txtEtiqueta.text.toString()) -> {
                        val usuarioExistenteMsg = Toast.makeText(
                            this,
                            "La etiqueta ya existe",
                            Toast.LENGTH_SHORT
                        )
                        usuarioExistenteMsg.show()
                    }
                    // Se registra la etiqueta
                    else -> {
                        agregarNuevaEtiqueta(txtEtiqueta.text.toString(),lista)
                        dialog.dismiss()
                    }
                }
            }
        // Cancelar
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                spinnerEtiqueta.setSelection(listaEtiquetas.indexOf(actividad.etiqueta.toString()))
                dialog.dismiss()
            }
    }

    fun agregarNuevaEtiqueta(etiqueta: String, lista: Lista?) {
        val posicion = listaEtiquetas.size - 1
        listaEtiquetas.add(posicion, etiqueta)
        lista?.etiquetas?.add(Etiqueta(etiqueta))

        spinnerEtiqueta.setSelection(posicion)

        val listaDoc = db.collection("Lista").document(lista?.id.toString())

        db.runTransaction{ transaction ->
            transaction.update(
                listaDoc,
                "etiquetas", lista?.etiquetas?.map{it.nombre}!!.toMutableList()
            )
        }
    }

}