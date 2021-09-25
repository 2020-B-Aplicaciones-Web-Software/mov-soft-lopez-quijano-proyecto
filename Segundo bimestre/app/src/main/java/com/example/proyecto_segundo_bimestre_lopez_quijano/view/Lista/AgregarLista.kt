package com.example.proyecto_segundo_bimestre_lopez_quijano.view.Lista

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_segundo_bimestre_lopez_quijano.R
import com.example.proyecto_segundo_bimestre_lopez_quijano.autenticacion.UsuarioAutorizado
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AgregarLista : AppCompatActivity() {

    // Referencias Firestore
    val db = Firebase.firestore
    val coleccionLista = db.collection("Lista")
    val coleccionUsuario = db.collection("Usuario")

    // Lista de usuarios (miembros)
    val listaUsuarios: MutableList<Usuario> = ArrayList()
    val datosListView: MutableList<String> = ArrayList()

    // Intent
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401

    // Campos
    lateinit var txtTitulo: EditText
    lateinit var listViewMiembros: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_lista)

        // Datos de la nueva lista
        val usuarioActual = Usuario(null, null, null, UsuarioAutorizado.email)
        listaUsuarios.add(usuarioActual)

        // Campos
        txtTitulo = findViewById(R.id.et_tituloListaCrear)
        listViewMiembros = findViewById(R.id.lv_miembrosListaCrear)


        // Agregar usuario actual a lista de miembros
        obtenerUsuarios()
        listaUsuarios.removeAt(0)   // Remover el usuario vacio
        listViewMiembros.setOnItemClickListener { adapterView, view, position, l ->
            if (position == datosListView.size - 1) {
                mostrarDialogoNuevoMiembro()
            }
        }

        // Boton Crear Lista
        val btnCrearLista = findViewById<Button>(R.id.btn_listaCrear)
        btnCrearLista.setOnClickListener {
            if (txtTitulo.text.isEmpty()) {
                val msg = Toast.makeText(this, "Ingrese un título válido", Toast.LENGTH_SHORT)
                msg.show()
            } else {
                crearLista()
            }
        }

    }

    fun crearLista() {
        val id = coleccionLista.document().id
        val nuevaLista = Lista(
            id,
            txtTitulo.text.toString(),
            ArrayList(listaUsuarios),
            UsuarioAutorizado.email!!,
            arrayListOf<Etiqueta>()
        )

        coleccionLista
            .document(id)
            .set(mapOf(
                "id" to nuevaLista.id,
                "nombre" to nuevaLista.nombre,
                "correo_propietario" to nuevaLista.correoPropietario,
                "usuarios" to nuevaLista.usuarios!!.map{it.correo},
                "etiquetas" to nuevaLista.etiquetas,
            ))
            .addOnSuccessListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Lista creada con éxito")
                builder.setPositiveButton("Aceptar") { _, _ ->
                    val intentExplicito = Intent(this, ListaDeActividades::class.java)
                    intentExplicito.putExtra("listaSeleccionada", nuevaLista)
                    startActivityForResult(intentExplicito, CODIGO_RESPUESTA_INTENT_EXPLICITO)
                }
                val dialogo = builder.create()
                dialogo.setCancelable(false)
                dialogo.show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext,"Error al crear la Lista",Toast.LENGTH_SHORT).show()
            }
    }

    fun obtenerUsuarios() {
        coleccionUsuario
            .whereIn("correo", listaUsuarios.map { it.correo }!!.toMutableList())
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { doc ->
                    listaUsuarios.add(Usuario(
                        doc["uid"].toString(),
                        doc["nombre"].toString(),
                        doc["apellido"].toString(),
                        doc["correo"].toString()
                    ))
                }
                actualizarListViewMiembros()
            }
    }

    fun actualizarListViewMiembros() {
        datosListView.clear()
        listaUsuarios.forEach {
            datosListView.add(it.toString())
        }
        datosListView.add("+ Agregar miembro")  // Opcion para agregar miembros

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            datosListView
        )
        listViewMiembros.adapter = adapter
    }

    fun mostrarDialogoNuevoMiembro() {
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
                // Campo vacio
                if (txtCorreo.text.isEmpty()) {
                    val msg = Toast.makeText(this, "Llene el campo de correo", Toast.LENGTH_SHORT)
                    msg.show()
                }
                // Usuario que ya es miembro de la lista
                else if ( listaUsuarios.map{it.correo}!!.contains(txtCorreo.text.toString()) ) {
                    val usuarioExistenteMsg = Toast.makeText(
                        this,
                        "Ese usuario ya es miembro de esta lista",
                        Toast.LENGTH_SHORT
                    )
                    usuarioExistenteMsg.show()
                }
                // Se registra el usuario si este existe en la BD
                else {
                    registrarSiEsValido(txtCorreo.text.toString())
                    dialog.dismiss()    // Cuando ya se registre
                }
            }
    }

    fun registrarSiEsValido(correo: String) {
        coleccionUsuario
            .whereEqualTo("correo", correo)
            .get()
            .addOnSuccessListener { documents ->
                if ( documents.size() == 1 ) {
                    val usuario = documents.documents[0]
                    listaUsuarios.add(Usuario(
                        usuario["uid"].toString(),
                        usuario["nombre"].toString(),
                        usuario["apellido"].toString(),
                        usuario["correo"].toString()
                    ))
                    actualizarListViewMiembros()
                } else {
                    val usuarioNoExistenteMsg = Toast.makeText(
                        this,
                        "Ese usuario no existe",
                        Toast.LENGTH_SHORT
                    )
                    usuarioNoExistenteMsg.show()
                }
            }
    }

}