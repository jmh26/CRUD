package com.example.firebase


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.days

@Parcelize
data class Gato(
    var id: String? = null,
    var raza: String? = null,
    var descripcion: String? = null,
    var edad: Int? = null,
    var calificacion: Float? = null,
    var fecha: String? =  null,
    var imagen: String? = null
):Parcelable