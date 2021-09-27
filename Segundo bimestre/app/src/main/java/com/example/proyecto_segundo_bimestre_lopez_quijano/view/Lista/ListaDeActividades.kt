package com.example.proyecto_segundo_bimestre_lopez_quijano.view.Lista

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import com.example.proyecto_segundo_bimestre_lopez_quijano.view.Actividad.CrearActividad
import com.example.proyecto_segundo_bimestre_lopez_quijano.R
import com.example.proyecto_segundo_bimestre_lopez_quijano.view.Actividad.VisualizarActividad
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
    lateinit var listaSeleccionada: Lista

    // Referencias Firestore
    val db = Firebase.firestore
    val coleccionLista = db.collection("Lista")

    // Listas obtenidas
    val listaListas: MutableList<Lista> = ArrayList()
    val listaActividades: MutableList<Actividad> = ArrayList()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityListaDeActividadesBinding

    // Spinner
    lateinit var spinnerFiltro: Spinner

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
                listaActividades[position],
                listaSeleccionada
            )
        }

        // Configurar lista
        val botonConfigurarLista = findViewById<Button>(R.id.btn_configurar_lista)
        botonConfigurarLista.setOnClickListener {
            abrirActividadEnviandoLista(
                ConfigurarLista::class.java,
                listaSeleccionada
            )
        }

        // Agregar Actividad
        val botonAgregarActividad = findViewById<Button>(R.id.btn_agregar_nueva_actividad)
        botonAgregarActividad.setOnClickListener {
            abrirActividadEnviandoLista(
                CrearActividad::class.java,
                listaSeleccionada
            )
        }

        // Filtros
        spinnerFiltro = findViewById(R.id.sp_filtro)
        obtenerFiltros()
        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (Actividad.OPCIONES_FILTRO[position]) {
                    Actividad.FECHA_ASCENDENTE -> {
                        Log.i("asd", "Filtro Ascendente")
                    }
                    Actividad.FECHA_DESCENDENTE -> {
                        Log.i("asd", "Filtro Descendente")
                    }
                    Actividad.FILTRO_PRIORIDAD -> {
                        filtrarPorPrioridad()
                    }
                    Actividad.FILTRO_ETIQUETA -> {
                        filtrarPorEtiqueta()
                    }
                }
                spinnerFiltro.setSelection(0)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
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

        // Mostrar siempre la primera lista si no se regresa de una lista diferente
        val listaIntent = intent.getParcelableExtra<Lista>("listaSeleccionada")
        listaSeleccionada = listaIntent ?: listaListas[0]
        mostrarActividadesDeListaActual(listaSeleccionada)

        // Agrega las opciones al menu y su funcionalidad
        listaListas.forEach { lista ->
            subMenu.add(lista.nombre)
                .setIcon(resources.getDrawable(R.drawable.ic_list_icon))
                .setOnMenuItemClickListener {
                    mostrarActividadesDeListaActual(lista)
                    listaSeleccionada = lista
                    drawerLayout.closeDrawers()
                    return@setOnMenuItemClickListener false
                }
        }

        // Boton para agregar listas
        subMenu.add("Agregar lista")
            .setIcon(resources.getDrawable(R.drawable.ic_add_list))
            .setOnMenuItemClickListener {
                abrirActividad(CrearLista::class.java)
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
                actualizarListView(listaActividades)
            }
    }

    fun actualizarListView(actividades: MutableList<Actividad>) {
        val listViewActividades = findViewById<ListView>(R.id.lv_actividades)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            actividades
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

    fun abrirActividadEnviandoActividad(clase: Class<*>, actividad: Actividad, lista: Lista) {
        val intentExplicito = Intent(this, clase)
        intentExplicito.putExtra("actividad", actividad)
        intentExplicito.putExtra("lista", lista)
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

    fun obtenerFiltros() {
        val opciones = Actividad.OPCIONES_FILTRO

        val adapter = object: ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            opciones
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                }
                return view
            }
        }

        spinnerFiltro.adapter = adapter
    }

    fun filtrarPorPrioridad() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escoja las prioridades que desea ver")

        val prioridadesFiltradas: MutableList<Int> = ArrayList()

        builder.setMultiChoiceItems(
            Array(Actividad.MIN_PRIORIDAD) { (it+1).toString() },
            BooleanArray(Actividad.MIN_PRIORIDAD)
        ) { _, index, isChecked ->
            if (isChecked) {
                prioridadesFiltradas.add(index + 1)
            } else {
                prioridadesFiltradas.remove(index + 1)
            }
        }

        builder
            .setPositiveButton("Filtrar", null)
            .setNegativeButton("Cancelar", null)

        val dialogo = builder.create()
        dialogo.setCancelable(false)
        dialogo.show()

        // Filtrar
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (prioridadesFiltradas.size == 0) {
                    val msg = Toast.makeText(this, "Escoja al menos una prioridad", Toast.LENGTH_SHORT)
                    msg.show()
                } else {
                    actualizarListView(
                        listaActividades.filter { actividad ->
                            return@filter prioridadesFiltradas.contains(actividad.prioridad)
                        }.toMutableList()
                    )
                    if (prioridadesFiltradas.size == Actividad.MIN_PRIORIDAD) {
                        marcarFiltroActivado(false)
                    } else {
                        marcarFiltroActivado(true)
                    }
                    dialogo.dismiss()
                }
            }
        // Cancelar
        dialogo.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                dialogo.dismiss()
            }
    }

    fun filtrarPorEtiqueta() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escoja la etiqueta que desea ver\n")

        val etiquetas: MutableList<Etiqueta> = ArrayList()
        etiquetas.add(Etiqueta(Etiqueta.SIN_ETIQUETA))
        etiquetas.addAll(listaSeleccionada.etiquetas!!)
        etiquetas.add(Etiqueta(Etiqueta.MOSTRAR_TODAS))
        val spinnerEtiqueta = Spinner(this)
        spinnerEtiqueta.adapter = ArrayAdapter(
            this, R.layout.support_simple_spinner_dropdown_item,
            etiquetas
        )
        val scale = resources.displayMetrics.density
        spinnerEtiqueta.minimumWidth = (10 * scale + 0.5f).toInt()
        spinnerEtiqueta.minimumHeight = (48 * scale + 0.5f).toInt()
        spinnerEtiqueta.setPadding(100, 100, 100, 100)
        spinnerEtiqueta.background = resources.getDrawable(R.drawable.sp_borders)
        builder.setView(spinnerEtiqueta)

        builder
            .setPositiveButton("Filtrar", null)
            .setNegativeButton("Cancelar", null)

        val dialogo = builder.create()
        dialogo.setCancelable(false)
        dialogo.show()

        // Filtrar
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {
                if (spinnerEtiqueta.selectedItemPosition != etiquetas.size - 1) {
                    actualizarListView(
                        listaActividades.filter { actividad ->
                            return@filter actividad.etiqueta!! == spinnerEtiqueta.selectedItem
                        }.toMutableList()
                    )
                    marcarFiltroActivado(true)
                } else {
                    actualizarListView(listaActividades)
                    marcarFiltroActivado(false)
                }
                dialogo.dismiss()
            }
        // Cancelar
        dialogo.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setOnClickListener {
                dialogo.dismiss()
            }
    }

    fun marcarFiltroActivado(activado: Boolean) {
        if (activado) {
            spinnerFiltro.background = resources.getDrawable(R.drawable.sp_filter_on)
        } else {
            spinnerFiltro.background = resources.getDrawable(R.drawable.sp_filter_off)
        }
    }
}