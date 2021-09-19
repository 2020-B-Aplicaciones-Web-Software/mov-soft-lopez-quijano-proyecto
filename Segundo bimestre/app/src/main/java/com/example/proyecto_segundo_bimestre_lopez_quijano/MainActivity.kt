package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    val CODIGO_INICIO_SESION = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botonLogin = findViewById<Button>(R.id.btn_login)
        botonLogin.setOnClickListener {
            //abrir(VisualizarActividad::class.java)
        }

        val textRegistrar = findViewById<TextView>(R.id.tv_registrarUsuario)
        textRegistrar.setOnClickListener {
            abrir(RegistrarUsuarios::class.java)
        }

        val botonRegistrar = findViewById<Button>(R.id.btn_login)
        botonRegistrar.setOnClickListener {
            val correo = findViewById<EditText>(R.id.et_correoLogin).text.toString()
            val contrasena = findViewById<EditText>(R.id.et_contraseñaLogin).text.toString()
            //registrar(correo, contrasena)
            //llamarLoginUsuario()
            registrarUsuario(correo, contrasena)
        }


    }

    override fun onStart() {
        super.onStart()
        val usuarioActual: FirebaseUser? = mAuth.currentUser

    }

    fun registrarUsuario(correo: String, contrasena: String) {
        Log.i("asd", "1 ${correo} - ${contrasena}")
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener {
                Log.i("asd", "2 ${correo} - ${contrasena}")
                if (it.isSuccessful) {
                    val user: FirebaseUser? = mAuth.currentUser
                    Log.i("asd", " 3${correo} - ${contrasena}")
                    abrir(ListaDeActividades::class.java)
                } else {
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun abrir(actividad: Class<*>) {
        val intent = Intent(this, actividad)
        startActivity(intent)
    }

}