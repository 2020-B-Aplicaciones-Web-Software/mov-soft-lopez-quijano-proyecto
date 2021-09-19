package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botonLogin = findViewById<Button>(R.id.btn_login)
        botonLogin.setOnClickListener {
            //abrir(VisualizarActividad::class.java)
        }

        val textRegistrar = findViewById<TextView>(R.id.tv_registrarUsuario)
        textRegistrar.setOnClickListener {
            abrirActividad(RegistrarUsuarios::class.java)
        }

    }

    override fun onStart() {
        super.onStart()
        val usuarioActual: FirebaseUser? = mAuth.currentUser

    }

    fun abrirActividad(actividad: Class<*>) {
        val intent = Intent(this, actividad)
        startActivity(intent)
    }

}