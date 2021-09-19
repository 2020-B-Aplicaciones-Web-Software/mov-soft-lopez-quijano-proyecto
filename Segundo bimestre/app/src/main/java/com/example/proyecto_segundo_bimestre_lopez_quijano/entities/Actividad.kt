package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

class Actividad(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fechaCreacion: String,
    val fechaVencimiento: String,
    val prioridad: Int,
    val etiqueta: Etiqueta,
    val usuarioCreador: Usuario
) {
    override fun toString(): String {
        return "$titulo\n$fechaVencimiento"
    }
}