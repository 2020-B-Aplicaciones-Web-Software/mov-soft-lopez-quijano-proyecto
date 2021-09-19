package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import com.example.proyecto_segundo_bimestre_lopez_quijano.databinding.ActivityListaDeActividadesBinding

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

    fun agregarItems(navView: NavigationView, drawerLayout: DrawerLayout) {
        val menu: Menu = navView.menu
        // TODO: Obtener los documentos de listas
        val listas: MutableList<String> = ArrayList<String>()
        listas.add("Lista 1")
        listas.add("Lista 2")
        listas.add("Lista 3")

        // Agrega las opciones al menu y agrega la funcion
        listas.forEachIndexed { i, it ->
            menu.add(0, i, i, it).setOnMenuItemClickListener {
                Log.i("asd", "CLICKEE ${it.itemId} ${it.title}")
                return@setOnMenuItemClickListener false
            }
        }
        drawerLayout.closeDrawers()
    }

    fun obtenerListaSeleccionada(navigation: NavigationView): Int {
        val menu = navigation.menu
        menu.forEachIndexed { i, item ->
            if (item.isChecked)
                return i
        }
        return 0
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