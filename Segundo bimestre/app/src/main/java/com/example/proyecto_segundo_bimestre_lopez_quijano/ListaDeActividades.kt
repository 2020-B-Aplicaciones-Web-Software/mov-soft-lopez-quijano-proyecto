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
import com.example.proyecto_segundo_bimestre_lopez_quijano.databinding.ActivityListaDeActividadesBinding
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Actividad
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Etiqueta
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario

class ListaDeActividades : AppCompatActivity() {

    // Intent Explicito
    val CODIGO_RESPUESTA_INTENT_EXPLICITO = 401

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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_lista_actividades// TODO: Ver si se pone algo aqui //R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        agregarListasAlMenu(navView, drawerLayout)

        val listViewActividades = findViewById<ListView>(R.id.lv_actividades)
        listViewActividades.setOnItemClickListener { adapterView, view, position, l ->
            abrirActividadEnviandoActividad(
                VisualizarActividad::class.java,
                listaActividades[position]
            )
        }

        val botonConfigurarLista = findViewById<Button>(R.id.btn_configurar_lista)
        botonConfigurarLista.setOnClickListener {
            abrirActividadEnviandoLista(
                ConfigurarLista::class.java,
                listaListas[indiceListaSeleccionada]
            )
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    // TODO: ELIMINAR
    fun datosEjemplo(): ArrayList<Lista> {
        val usuarioEjemplo = Usuario(
            "ID_usuario_1",
            "Juan",
            "Suasnabas",
            "user@email.com",
            "hash123"
        )
        val listas = ArrayList<Lista>()
        listas.add(Lista("ID_lista_1", "Lista 1", arrayListOf(usuarioEjemplo), "ID_usuario_1"))
        listas.add(Lista("ID_lista_2", "Lista 2", arrayListOf(usuarioEjemplo), "ID_usuario_1"))
        listas.add(Lista("ID_lista_3", "Lista 3", arrayListOf(usuarioEjemplo), "ID_usuario_1"))
        return listas
    }
    fun datosEjemploActividades(): ArrayList<Actividad> {
        val usuarioEjemplo = Usuario(
            "ID_usuario_1",
            "Juan",
            "Suasnabas",
            "user@email.com",
            "hash123"
        )
        val actividades = ArrayList<Actividad>()
        actividades.add(Actividad(
            "ID_act_1",
            "Actividad 1",
            "Descripcion Act1",
            "01/01/2000",
            "01/01/2021",
            5,
            Etiqueta("ID_et_1", "Etiqueta 1", "Descripcion Et1"),
            usuarioEjemplo
        ))
        actividades.add(Actividad(
            "ID_act_2",
            "Actividad 2",
            "Descripcion Act2",
            "01/01/2000",
            "01/01/2021",
            4,
            Etiqueta("ID_et_1", "Etiqueta 1", "Descripcion Et1"),
            usuarioEjemplo
        ))
        actividades.add(Actividad(
            "ID_act_3",
            "Actividad 3",
            "Descripcion Act3",
            "01/01/2000",
            "01/01/2021",
            5,
            Etiqueta("ID_et_2", "Etiqueta 2", "Descripcion Et2"),
            usuarioEjemplo
        ))
        return actividades
    }


    fun agregarListasAlMenu(navView: NavigationView, drawerLayout: DrawerLayout) {
        val menu: Menu = navView.menu
        val subMenu: SubMenu = menu.addSubMenu("Listas")

        // TODO: Obtener los documentos de listas
        datosEjemplo().forEach {
            listaListas.add(it)
        }

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
            .setOnMenuItemClickListener {
                abrirActividad(AgregarLista::class.java)
                drawerLayout.closeDrawers()
                return@setOnMenuItemClickListener false
            }

    }

    fun mostrarActividadesDeListaActual(lista: Lista) {
        listaActividades.clear()
        this.setTitle(lista.nombre)

        // TODO: Obtener actividades de la Lista
        datosEjemploActividades().forEach {
            listaActividades.add(it)
        }

        val listViewActividades = findViewById<ListView>(R.id.lv_actividades)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listaActividades
        )
        listViewActividades.adapter = adapter
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