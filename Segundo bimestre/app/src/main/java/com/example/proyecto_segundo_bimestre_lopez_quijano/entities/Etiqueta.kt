package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

import android.os.Parcel
import android.os.Parcelable

class Etiqueta(
    val nombre: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
    }

    override fun toString(): String {
        return nombre!!
    }

    override fun equals(other: Any?): Boolean {
        if (other is Etiqueta)
            return this.nombre.equals(other.nombre)
        return false
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Etiqueta> {

        const val SIN_ETIQUETA = "[Sin etiqueta]"
        const val AGREGAR_ETIQUETA = "+ Añadir etiqueta"
        const val MOSTRAR_TODAS = "Mostrar todas"

        override fun createFromParcel(parcel: Parcel): Etiqueta {
            return Etiqueta(parcel)
        }

        override fun newArray(size: Int): Array<Etiqueta?> {
            return arrayOfNulls(size)
        }
    }
}