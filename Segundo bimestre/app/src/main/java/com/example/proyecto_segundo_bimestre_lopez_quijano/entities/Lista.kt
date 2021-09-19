package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

class Lista(
    val id: String,
    val nombre: String,
    val usuarios: ArrayList<Usuario>,
    val idPropietario: String
) {
    override fun toString(): String {
        return nombre
    }
}