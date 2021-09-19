package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

import android.os.Parcel
import android.os.Parcelable

class Etiqueta(
    val id: String?,
    val nombre: String?,
    val descripcion: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun toString(): String {
        return nombre!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Etiqueta> {
        override fun createFromParcel(parcel: Parcel): Etiqueta {
            return Etiqueta(parcel)
        }

        override fun newArray(size: Int): Array<Etiqueta?> {
            return arrayOfNulls(size)
        }
    }
}