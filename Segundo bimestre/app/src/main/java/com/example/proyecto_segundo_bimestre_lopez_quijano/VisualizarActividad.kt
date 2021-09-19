package com.example.proyecto_segundo_bimestre_lopez_quijano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class VisualizarActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_actividad)

    }

    override fun onStop() {
        //TODO: usar esto para actualizar :3 o el onDestroy
        //TODO: teoricamente cuando nos salimos se activa el de destruir pero antes se pausa
        super.onStop()
    }
}