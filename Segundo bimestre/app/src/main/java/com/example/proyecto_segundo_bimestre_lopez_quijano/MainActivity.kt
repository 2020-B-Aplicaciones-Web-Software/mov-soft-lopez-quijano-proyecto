package com.example.proyecto_segundo_bimestre_lopez_quijano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.appcompat.app.AlertDialog
import com.example.proyecto_segundo_bimestre_lopez_quijano.autenticacion.UsuarioAutorizado


class MainActivity : AppCompatActivity() {

    //Instancia de firebase authentication
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Ir a la pantalla de registro
        val textRegistrar = findViewById<TextView>(R.id.tv_registrarUsuario)
        textRegistrar.setOnClickListener {
            abrirActividad(RegistrarUsuarios::class.java)
        }

        //Iniciar Sesión
        val botonLogin = findViewById<Button>(R.id.btn_login)
        botonLogin.setOnClickListener {
            val correo = findViewById<EditText>(R.id.et_correoLogin).text.toString()
            val contrasena = findViewById<EditText>(R.id.et_contrasenaLogin).text.toString()
            iniciarSesion(correo,contrasena)
        }

    }

    fun iniciarSesion(correo : String, contrasena : String){

        //Verificar que no sean vacios
        if(!correo.isBlank()  && !contrasena.isBlank()){
            //Verificar longitud de la contraseña
            if(contrasena.length>=6){
                //Ingreso a la aplicación
                mAuth.signInWithEmailAndPassword(correo,contrasena)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            UsuarioAutorizado.email = correo
                            cargando()
                            abrirActividad(ListaDeActividades::class.java)
                            //Cierra la app si trata de regresar al login
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Problemas de registro de usuario, compruebe los datos", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(applicationContext,"La contraseña debe tener al menos 6 caracteres",Toast.LENGTH_SHORT).show()
             }
        }else{
            Toast.makeText(applicationContext,"Rellene los campos faltantes",Toast.LENGTH_SHORT).show()
        }
    }

    fun cargando(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ingresando a la aplicación")
        builder.setMessage("Espere por favor")
        val dialogo = builder.create()
        dialogo.show()
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