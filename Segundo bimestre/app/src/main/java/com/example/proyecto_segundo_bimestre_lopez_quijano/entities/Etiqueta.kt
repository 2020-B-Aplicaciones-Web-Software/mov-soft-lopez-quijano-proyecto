package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

class Etiqueta(
    val id: String,
    val nombre: String,
    val descripcion: String
) {
    override fun toString(): String {
        return nombre
    }
}