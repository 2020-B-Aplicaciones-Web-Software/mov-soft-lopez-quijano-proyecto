package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

import android.os.Parcel
import android.os.Parcelable

class Lista(
    val id: String?,
    val nombre: String?,
    val usuarios: ArrayList<Usuario>?,
    val correoPropietario: String?,
    val etiquetas: ArrayList<Etiqueta>?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        arrayListOf<Usuario>().apply {
            parcel.readList(this, Usuario.javaClass.classLoader)
        },
        parcel.readString(),
        arrayListOf<Etiqueta>().apply {
            parcel.readList(this, Etiqueta.javaClass.classLoader)
        }
    ) {
    }

    override fun toString(): String {
        return nombre!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeList(usuarios)
        parcel.writeString(correoPropietario)
        parcel.writeList(etiquetas)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Lista> {
        override fun createFromParcel(parcel: Parcel): Lista {
            return Lista(parcel)
        }

        override fun newArray(size: Int): Array<Lista?> {
            return arrayOfNulls(size)
        }
    }
}