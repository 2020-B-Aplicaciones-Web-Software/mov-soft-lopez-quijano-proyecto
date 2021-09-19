package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val boton = findViewById<Button>(R.id.btn_nav)
        boton.setOnClickListener {
            abrir(VisualizarActividad::class.java)
        }

        val botonListar = findViewById<Button>(R.id.btn_listar)
        botonListar.setOnClickListener {
            abrir(ListaDeActividades::class.java)
        }
    }

    fun abrir(actividad: Class<*>){
        val intent = Intent(this, actividad)
        startActivity(intent)
    }
}