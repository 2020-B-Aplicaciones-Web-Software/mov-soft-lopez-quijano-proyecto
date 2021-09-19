package com.example.proyecto_segundo_bimestre_lopez_quijano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Lista
import com.example.proyecto_segundo_bimestre_lopez_quijano.entities.Usuario

class CrearActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_actividad)

        // TODO: Obtener los datos del intent

        val usuarioEjemplo = Usuario(
            "ID_usuario_1",
            "Juan",
            "Suasnabas",
            "user@email.com",
            "hash123"
        )
        val lista = Lista("ID_lista_1", "Lista 1", arrayListOf(usuarioEjemplo), "ID_usuario_1")

        // Cambiar el título de la actividad al nombre de la lista
        val nombreLista = lista.nombre
        this.setTitle(nombreLista);
    }
}