package com.example.proyecto_segundo_bimestre_lopez_quijano.entities

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

class Actividad(
    val id: String?,
    val titulo: String?,
    val descripcion: String?,
    val fechaCreacion: Date?,
    val fechaVencimiento: Date?,
    val prioridad: Int,
    val etiqueta: Etiqueta?,
    val usuarioCreador: Usuario?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Date::class.java.classLoader) as? Date,
        parcel.readValue(Date::class.java.classLoader) as? Date,
        parcel.readInt(),
        parcel.readParcelable(Etiqueta::class.java.classLoader),
        parcel.readParcelable(Usuario::class.java.classLoader)
    ) {
    }

    override fun toString(): String {
        val format = SimpleDateFormat("dd/MM/yyyy")
        return "$titulo\nPara el ${format.format(fechaVencimiento)}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(titulo)
        parcel.writeString(descripcion)
        parcel.writeValue(fechaCreacion)
        parcel.writeValue(fechaVencimiento)
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