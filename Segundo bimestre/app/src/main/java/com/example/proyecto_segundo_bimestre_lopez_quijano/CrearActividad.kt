package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.Touch
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_segundo_bimestre_lopez_quijano.autenticacion.UsuarioAutorizado
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CrearActividad : AppCompatActivity() {

    // Referencias Firestore
    val db = Firebase.firestore
    // Arreglos
    val spinnerPrioridad: MutableList<String> = ArrayList()
    val spinnerEtiquetas: MutableList<String> = ArrayList()

    //Usuario
    var usuario = Usuario(null,null,null,null)

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
    }

    fun guardarActividad(idLista : String){

        // Obtener fecha del momento
        val formatoFecha = SimpleDateFormat("dd/M/yyyy")
        val fecha_creacion = formatoFecha.format(Date())

        // Información a incluir
        val titulo = findViewById<EditText>(R.id.et_tituloCrear).text.toString()
        val descripcion = findViewById<EditText>(R.id.et_descripcionCrear).text.toString()
        val fecha_vencimiento = findViewById<EditText>(R.id.et_fechaVencimientoCrear).text.toString()
        val prioridad = findViewById<Spinner>(R.id.sp_prioridadCrear).selectedItem.toString()
        val etiqueta = findViewById<Spinner>(R.id.sp_etiquetaCrear).selectedItem.toString()

        if(!titulo.isBlank() && !fecha_creacion.isBlank()){
            // Referencia de la sub coleccion
            val coleccionActividad = db.collection("Lista/${idLista}/Actividad")
            val id = coleccionActividad.document().id

            //Organizando la estructura
            val usuarioHM = hashMapOf<String, Any>(
                "nombre" to usuario.nombre.toString(),
                "apellido" to usuario.apellido.toString()
            )

            val nuevaActividad = hashMapOf<String,Any>(
                "descripcion" to descripcion,
                "etiqueta" to etiqueta,
                "fecha_creacion" to fecha_creacion,
                "fecha_vencimiento" to fecha_vencimiento,
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

        val coleccionUsuario = db.collection("Usuario")
            .document(correo)
            .get()
            .addOnSuccessListener {
                Log.i("asd","${it}")
                Log.i("asd","${it["nombre"]}")
                Log.i("asd","${it["apellido"]}")
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

    fun obtenerEtiquetas(etiqueta: ArrayList<Etiqueta>){

        for (nombre in etiqueta){
            spinnerEtiquetas.add(nombre.toString())
        }

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, spinnerEtiquetas
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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