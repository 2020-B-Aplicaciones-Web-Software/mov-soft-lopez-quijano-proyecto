package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

import android.os.Parcel
import android.os.Parcelable

class Actividad(
    val id: String?,
    val titulo: String?,
    val descripcion: String?,
    val fechaCreacion: String?,
    val fechaVencimiento: String?,
    val prioridad: Int,
    val etiqueta: Etiqueta?,
    val usuarioCreador: Usuario?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(Etiqueta::class.java.classLoader),
        parcel.readParcelable(Usuario::class.java.classLoader)
    ) {
    }

    override fun toString(): String {
        return "$titulo\n$fechaVencimiento"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(titulo)
        parcel.writeString(descripcion)
        parcel.writeString(fechaCreacion)
        parcel.writeString(fechaVencimiento)
        parcel.writeInt(prioridad)
        parcel.writeParcelable(etiqueta, flags)
        parcel.writeParcelable(usuarioCreador, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Actividad> {
        override fun createFromParcel(parcel: Parcel): Actividad {
            return Actividad(parcel)
        }

        override fun newArray(size: Int): Array<Actividad?> {
            return arrayOfNulls(size)
        }
    }
}