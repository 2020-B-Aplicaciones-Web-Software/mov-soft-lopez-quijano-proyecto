package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_segundo_bimestre_lopez_quijano.databinding.ActivityListaDeActividadesBinding
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario

class ListaDeActividades : AppCompatActivity() {

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
        }*/

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_lista_de_actividades)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                // TODO: Ver si se pone algo aqui //R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        agregarItems(navView, drawerLayout)

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
            "01/01/2020",
            "hash123"
        )
        val listas = ArrayList<Lista>()
        listas.add(Lista("ID_lista_1", "Lista 1", arrayListOf(usuarioEjemplo), "ID_usuario_1"))
        listas.add(Lista("ID_lista_1", "Lista 1", arrayListOf(usuarioEjemplo), "ID_usuario_1"))
        listas.add(Lista("ID_lista_1", "Lista 1", arrayListOf(usuarioEjemplo), "ID_usuario_1"))
        return listas
    }

    fun agregarItems(navView: NavigationView, drawerLayout: DrawerLayout) {
        val menu: Menu = navView.menu

        // TODO: Obtener los documentos de listas
        val listas = datosEjemplo()

        // Agrega las opciones al menu y agrega la funcion
        listas.forEachIndexed { i, lista ->
            menu.add(0, i, i, lista.nombre)
                .setOnMenuItemClickListener { item ->
                    Log.i("asd", "CLICKEE ${item.itemId} ${item.title}")
                    return@setOnMenuItemClickListener false
                }
        }
        drawerLayout.closeDrawers()
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