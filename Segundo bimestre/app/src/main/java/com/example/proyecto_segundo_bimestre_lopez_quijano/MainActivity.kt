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
            abrir()
        }
    }
    fun abrir(){
        val intent = Intent(
            this,
            ListaDeActividades::class.java
        )
        startActivity(intent)
    }
}