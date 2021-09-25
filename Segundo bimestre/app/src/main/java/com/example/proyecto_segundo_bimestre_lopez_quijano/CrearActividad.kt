package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.DialogInterface
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


class CrearActividad : AppCompatActivity() {

    // Referencias Firestore
    val db = Firebase.firestore
    // Arreglos
    val spinnerPrioridad: MutableList<String> = ArrayList()
    val spinnerEtiquetas: MutableList<String> = ArrayList()

    //Usuario
    var usuario = Usuario(null,null,null,null)
    var bandera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_actividad)

        // Obtener Intent
        val lista = intent.getParcelableExtra<Lista>("lista")

        // Obtener Etiquetas
        obtenerEtiquetas(lista?.etiquetas!!)

        // Obtener Prioridad
        obtenerPrioridad()

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

        val sItems = findViewById<Spinner>(R.id.sp_etiquetaCrear)
        sItems.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if(bandera == false){
                    // Agregar nueva etiqueta
                    if (position == spinnerEtiquetas.size - 1) {
                        bandera = true
                        agregarNuevaEtiqueta(lista)
                        obtenerEtiquetas(lista.etiquetas)
                    }
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
        val prioridad = findViewById<Spinner>(R.id.sp_prioridadCrear).selectedItem.toString().toInt()
        val etiqueta = findViewById<Spinner>(R.id.sp_etiquetaCrear).selectedItem.toString()

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
        }else{
            Toast.makeText(applicationContext,"Llene el título y la fecha de vencimiento",Toast.LENGTH_SHORT).show()
        }
    }

    fun agregarNuevaEtiqueta(lista: Lista?) {
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
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                // Campo vacio
                if (txtEtiqueta.text.isEmpty()) {
                    val msg = Toast.makeText(this, "Llene el campo de etiqueta", Toast.LENGTH_SHORT)
                    msg.show()
                }
                // Etiqueta que ya es miembro de la lista
                else if (lista?.etiquetas!!.map { it.nombre }.contains(txtEtiqueta.text.toString())) {
                    val usuarioExistenteMsg = Toast.makeText(
                        this,
                        "La etiqueta ya existe",
                        Toast.LENGTH_SHORT
                    )
                    usuarioExistenteMsg.show()
                }
                // Se registra la etiqueta
                else {
                    agregarEtiqueta(txtEtiqueta.text.toString(),lista)
                    dialog.dismiss()
                }
                bandera = false
            }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                val sItems = findViewById<Spinner>(R.id.sp_etiquetaCrear)
                sItems.setSelection(0)
                bandera = false
                dialog.dismiss()
            }
    }

    fun agregarEtiqueta(etiqueta: String, lista: Lista?) {
        val posicion = spinnerEtiquetas.size - 1
        spinnerEtiquetas.add(posicion, etiqueta)
        lista?.etiquetas?.add(Etiqueta(etiqueta))

        val sItems = findViewById<Spinner>(R.id.sp_etiquetaCrear)
        sItems.setSelection(posicion)

        val listaDoc = db.collection("Lista").document(lista?.id.toString())

        db.runTransaction{ transaction ->
            transaction.update(listaDoc,"etiquetas", lista?.etiquetas?.map{it.nombre}!!.toMutableList())
        }
    }

    fun abrirMensaje(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Actividad creada con éxito")
        builder.setPositiveButton(
            "Aceptar",
            DialogInterface.OnClickListener{ dialog, which ->
                val intent = Intent(this, ListaDeActividades::class.java)
                startActivity(intent)
            }
        )
        val dialogo = builder.create()
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
        spinnerEtiquetas.clear()
        spinnerEtiquetas.add("[Sin etiqueta]")
        for (nombre in etiquetas){
            spinnerEtiquetas.add(nombre.toString())
        }
        spinnerEtiquetas.add("+ Añadir etiqueta")

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, spinnerEtiquetas
        )
        val sItems = findViewById<Spinner>(R.id.sp_etiquetaCrear)

        sItems.adapter = adapter
    }

    fun obtenerPrioridad(){

        for (i in 1..5){
            spinnerPrioridad.add(i.toString())
        }

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerPrioridad
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val sItems = findViewById<Spinner>(R.id.sp_prioridadCrear)
        sItems.adapter = adapter
    }
}