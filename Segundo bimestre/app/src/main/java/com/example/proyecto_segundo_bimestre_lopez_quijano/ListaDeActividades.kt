package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.SubMenu
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_segundo_bimestre_lopez_quijano.autenticacion.UsuarioAutorizado
import com.example.proyecto_segundo_bimestre_lopez_quijano.databinding.ActivityListaDeActividadesBinding
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Actividad
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListaDeActividades : AppCompatActivity() {

    // Intent Explicito
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401

    // Referencias Firestore
    val db = Firebase.firestore
    val coleccionLista = db.collection("Lista")

    // Listas obtenidas
    val listaListas: MutableList<Lista> = ArrayList()
    val listaActividades: MutableList<Actividad> = ArrayList()
    var indiceListaSeleccionada: Int = 0

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityListaDeActividadesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListaDeActividadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarListaDeActividades.toolbar)

        /* TODO: Borrar lo relacionado (si no esta ya borrado) a esto para que no ocupe espacio

        binding.appBarListaDeActividades.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        */


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_lista_de_actividades)

        // Configurar fragmentos top level
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_lista_actividades), drawerLayout
        )

        // Agregar items al menu desplegable
        obtenerListas()

        // List View
        val listViewActividades = findViewById<ListView>(R.id.lv_actividades)
        listViewActividades.setOnItemClickListener { adapterView, view, position, l ->
            abrirActividadEnviandoActividad(
                VisualizarActividad::class.java,
                listaActividades[position]
            )
        }

        // Configurar lista
        val botonConfigurarLista = findViewById<Button>(R.id.btn_configurar_lista)
        botonConfigurarLista.setOnClickListener {
            abrirActividadEnviandoLista(
                ConfigurarLista::class.java,
                listaListas[indiceListaSeleccionada]
            )
        }

        // Agregar Actividad
        val botonAgregarActividad = findViewById<Button>(R.id.btn_agregar_nueva_actividad)
        botonAgregarActividad.setOnClickListener {
            abrirActividadEnviandoLista(
                CrearActividad::class.java,
                listaListas[indiceListaSeleccionada]
            )
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    fun obtenerListas() {
        // Obtener documentas de Lista
        coleccionLista
            .whereArrayContains("usuarios", UsuarioAutorizado.email!!)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { doc ->
                    // Usuarios de la lista
                    val usuariosArray = doc["usuarios"] as ArrayList<String>
                    val usuarios = usuariosArray.map { user ->
                        Usuario(null, null, null, user)
                    }
                    // Etiquetas de la lista
                    val etiquetasArray = doc["etiquetas"] as ArrayList<String>
                    val etiquetas = etiquetasArray.map { etiqueta ->
                        Etiqueta(etiqueta)
                    }
                    // Lista
                    listaListas.add(Lista(
                        doc["id"].toString(),
                        doc["nombre"].toString(),
                        ArrayList(usuarios),
                        doc["correo_propietario"].toString(),
                        ArrayList(etiquetas)
                    ))
                }
                // Actualizar vista
                val drawerLayout: DrawerLayout = binding.drawerLayout
                val navView: NavigationView = binding.navView
                agregarListasAlMenu(navView, drawerLayout)
            }
    }

    fun agregarListasAlMenu(navView: NavigationView, drawerLayout: DrawerLayout) {
        val menu: Menu = navView.menu
        val subMenu: SubMenu = menu.addSubMenu("Listas")

        // Mostrar siempre la primera lista
        mostrarActividadesDeListaActual(listaListas[0])

        // Agrega las opciones al menu y su funcionalidad
        listaListas.forEachIndexed { index, lista ->
            // TODO: Agregar icono?
            subMenu.add(lista.nombre)
                .setOnMenuItemClickListener {
                    mostrarActividadesDeListaActual(lista)
                    indiceListaSeleccionada = index
                    drawerLayout.closeDrawers()
                    return@setOnMenuItemClickListener false
                }
        }

        // Boton para agregar listas
        // TODO: icono de '+'
        subMenu.add("Agregar lista")
            .setIcon(resources.getDrawable(R.drawable.ic_add_list))
            .setOnMenuItemClickListener {
                abrirActividad(AgregarLista::class.java)
                drawerLayout.closeDrawers()
                return@setOnMenuItemClickListener false
            }
    }

    fun mostrarActividadesDeListaActual(lista: Lista) {
        listaActividades.clear()
        this.setTitle(lista.nombre)

        // Obtener actividades de Lista
        val subcoleccionActividad = db.collection("Lista/${lista.id}/Actividad")
        subcoleccionActividad.get()
            .addOnSuccessListener { documents ->
                documents.forEach { doc ->
                    // Usuario
                    val usuarioMap = doc["usuario_creador"] as HashMap<String, Any>
                    val usuarioCreador = Usuario(
                        null,
                        usuarioMap["nombre"].toString(),
                        usuarioMap["apellido"].toString(),
                        null
                    )
                    val fechaCreacion = doc["fecha_creacion"] as Timestamp
                    val fechaVencimiento = doc["fecha_vencimiento"] as Timestamp
                    // Actividad
                    listaActividades.add(Actividad(
                        doc["id"].toString(),
                        doc["titulo"].toString(),
                        doc["descripcion"].toString(),
                        fechaCreacion.toDate(),
                        fechaVencimiento.toDate(),
                        doc["prioridad"].toString().toInt(),
                        Etiqueta(doc["etiqueta"].toString()),
                        usuarioCreador
                    ))
                }
                val listViewActividades = findViewById<ListView>(R.id.lv_actividades)
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    listaActividades
                )
                listViewActividades.adapter = adapter
            }
    }

    fun abrirActividad(clase: Class<*>) {
        val intent = Intent(this, clase)
        startActivity(intent)
    }

    fun abrirActividadEnviandoLista(clase: Class<*>, lista: Lista) {
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("lista", lista)
        startActivityForResult(intentExplicito, CODIGO_RESPUESTA_INTENT_EXPLICITO)
    }

    fun abrirActividadEnviandoActividad(clase: Class<*>, actividad: Actividad) {
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("actividad", actividad)
        startActivityForResult(intentExplicito, CODIGO_RESPUESTA_INTENT_EXPLICITO)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.lista_de_actividades, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_lista_de_actividades)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}