package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

import android.os.Parcel
import android.os.Parcelable

class Lista(
    val id: String?,
    val nombre: String?,
    val usuarios: ArrayList<Usuario>?,
    val idPropietario: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readArrayList(Usuario.javaClass.classLoader) as ArrayList<Usuario>?,
        parcel.readString()
    ) {
    }

    override fun toString(): String {
        return nombre!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeArray(arrayOf(usuarios))
        parcel.writeString(idPropietario)
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