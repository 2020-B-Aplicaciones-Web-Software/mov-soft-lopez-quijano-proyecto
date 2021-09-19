package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

class Usuario(
    val id: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val fechaNacimiento: String,
    val contrasena: String  // TODO: Hash?
) {
    override fun toString(): String {
        return "$nombre $apellido"
    }
}