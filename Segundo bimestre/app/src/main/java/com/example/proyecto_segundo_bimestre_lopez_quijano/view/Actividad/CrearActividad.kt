package com.example.proyecto_segundo_bimestre_lopez_quijano.view.Actividad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_segundo_bimestre_lopez_quijano.autenticacion.UsuarioAutorizado
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.AdapterView
import com.example.proyecto_segundo_bimestre_lopez_quijano.R
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Actividad
import com.example.proyecto_segundo_bimestre_lopez_quijano.view.Lista.ListaDeActividades


class CrearActividad : AppCompatActivity() {

    // Referencias Firestore
    val db = Firebase.firestore

    // Arreglos
    val listaPrioridades: MutableList<String> = ArrayList()
    val listaEtiquetas: MutableList<String> = ArrayList()

    // Intent
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401

    //Usuario
    lateinit var usuario : Usuario

    // Spinners
    lateinit var spinnerEtiquetas: Spinner
    lateinit var spinnerPrioridad: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_actividad)

        // Obtener Intent
        val lista = intent.getParcelableExtra<Lista>("lista")

        // Campos
        spinnerEtiquetas = findViewById(R.id.sp_etiquetaCrear)
        spinnerPrioridad = findViewById(R.id.sp_prioridadCrear)

        // Obtener Etiquetas
        obtenerEtiquetas(lista?.etiquetas!!)

        // Obtener Prioridad
        obtenerPrioridades()

        // Obtener usuario
        obtenerUsuario(UsuarioAutorizado.email.toString())

        // Cambiar el título de la actividad al nombre de la lista
        val nombreLista = lista?.nombre
        this.setTitle(nombreLista);

        // Guardar nueva Actividad
        val botonGuardar = findViewById<Button>(R.id.btn_actividadCrear)
        botonGuardar.setOnClickListener {
           guardarActividad(lista?.id!!)
        }

        spinnerEtiquetas.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                position: Int, id: Long
            ) {
                // Agregar nueva etiqueta
                if (position == listaEtiquetas.size - 1) {
                    // bandera = true
                    mostrarDialogoNuevaEtiqueta(lista)
                    obtenerEtiquetas(lista.etiquetas)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

    }

    fun guardarActividad(idLista : String){

        // Obtener fecha del momento
        val fecha_creacion = Timestamp(Date())

        // Información a incluir
        val titulo = findViewById<EditText>(R.id.et_tituloCrear).text.toString()
        val descripcion = findViewById<EditText>(R.id.et_descripcionCrear).text.toString()
        val fecha_vencimiento = findViewById<EditText>(R.id.et_fechaVencimientoCrear).text.toString()
        val prioridad = spinnerPrioridad.selectedItem.toString().toInt()
        val etiqueta = spinnerEtiquetas.selectedItem.toString()

        if(!titulo.isBlank() && !fecha_vencimiento.isBlank()){
            // Referencia de la sub coleccion
            val coleccionActividad = db.collection("Lista/${idLista}/Actividad")
            val id = coleccionActividad.document().id

            //Organizando la estructura
            val usuarioHM = hashMapOf<String, Any>(
                "nombre" to usuario.nombre.toString(),
                "apellido" to usuario.apellido.toString()
            )

            val formatoFecha = SimpleDateFormat("dd/M/yyyy")
            val fecha_vencimiento_date = formatoFecha.parse(fecha_vencimiento)

            val nuevaActividad = hashMapOf<String,Any>(
                "descripcion" to descripcion,
                "etiqueta" to etiqueta,
                "fecha_creacion" to fecha_creacion,
                "fecha_vencimiento" to Timestamp(fecha_vencimiento_date),
                "id" to id,
                "prioridad" to prioridad,
                "titulo" to titulo,
                "usuario_creador" to usuarioHM
            )

            coleccionActividad
                .document(id)
                .set(nuevaActividad)
                .addOnSuccessListener {
                    abrirMensaje()
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext,"Error al crear la Actividad",Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(applicationContext,"Llene el título y la fecha de vencimiento",Toast.LENGTH_SHORT).show()
        }
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
              //  bandera = false
            }
        // Cancelar
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                spinnerEtiquetas.setSelection(0)
                dialog.dismiss()
            }
    }

    fun agregarNuevaEtiqueta(etiqueta: String, lista: Lista?) {
        val posicion = listaEtiquetas.size - 1
        listaEtiquetas.add(posicion, etiqueta)
        lista?.etiquetas?.add(Etiqueta(etiqueta))

        spinnerEtiquetas.setSelection(posicion)

        val listaDoc = db.collection("Lista").document(lista?.id.toString())

        db.runTransaction{ transaction ->
            transaction.update(
                listaDoc,
                "etiquetas", lista?.etiquetas?.map{it.nombre}!!.toMutableList()
            )
        }
    }

    fun abrirMensaje(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Actividad creada con éxito")
        builder.setPositiveButton("Aceptar") { _, _ ->
            val lista = intent.getParcelableExtra<Lista>("lista")
            val intentExplicito = Intent(this, ListaDeActividades::class.java)
            intentExplicito.putExtra("listaSeleccionada", lista)
            startActivityForResult(intentExplicito, CODIGO_RESPUESTA_INTENT_EXPLICITO)
        }
        val dialogo = builder.create()
        dialogo.setCancelable(false)
        dialogo.show()
    }

    fun obtenerUsuario(correo : String){
        db.collection("Usuario")
            .document(correo)
            .get()
            .addOnSuccessListener {
                usuario = Usuario(
                    null,
                    it["nombre"]?.toString(),
                    it["apellido"]?.toString(),
                    null
                )
            }
            .addOnFailureListener { exception ->
                Log.d("firebasedata", "Error getting documents: ", exception)
            }
    }

    fun obtenerEtiquetas(etiquetas: ArrayList<Etiqueta>){
        listaEtiquetas.clear()
        listaEtiquetas.add(Etiqueta.SIN_ETIQUETA)
        for (nombre in etiquetas){
            listaEtiquetas.add(nombre.toString())
        }
        listaEtiquetas.add(Etiqueta.AGREGAR_ETIQUETA)

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, listaEtiquetas
        )

        spinnerEtiquetas.adapter = adapter
    }

    fun obtenerPrioridades(){
        for (i in Actividad.MAX_PRIORIDAD .. Actividad.MIN_PRIORIDAD) {
            listaPrioridades.add(i.toString())
        }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, listaPrioridades
        )
        spinnerPrioridad.adapter = adapter
    }

    override fun onBackPressed() {
        val lista = intent.getParcelableExtra<Lista>("lista")
        val intentExplicito = Intent(this, ListaDeActividades::class.java)
        intentExplicito.putExtra("listaSeleccionada", lista)
        startActivityForResult(intentExplicito, CODIGO_RESPUESTA_INTENT_EXPLICITO)
    }
}