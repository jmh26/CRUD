package com.example.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class EditarGato :  AppCompatActivity(), CoroutineScope {

    private lateinit var raza : EditText
    private lateinit var descripcion : EditText
    private lateinit var edad :  EditText
    private lateinit var calificacion: RatingBar
    private lateinit var fecha: String
    private lateinit var imagen :  ImageView
    private lateinit var modificar: Button
    private lateinit var volver : Button

    private var url_gato: Uri?=null
    private lateinit var database_reference: DatabaseReference
    private lateinit var storage_reference : StorageReference
    private lateinit var pojo_gato:Gato
    private lateinit var lista_gatos: MutableList<Gato>

    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editar_gato)

        val this_activity = this
        job= Job()

        pojo_gato = intent.getParcelableExtra<Gato>("raza")!!

        raza = findViewById(R.id.raza)
        descripcion = findViewById(R.id.Descripcion)
        edad = findViewById(R.id.edad)
        modificar = findViewById(R.id.editar)
        calificacion = findViewById(R.id.rateargato)
        imagen = findViewById(R.id.imagengato)
        volver = findViewById(R.id.volver)
        raza.setText(pojo_gato.raza)
        descripcion.setText(pojo_gato.descripcion)
        edad.setText(pojo_gato.edad.toString())

        Glide.with(applicationContext).load(pojo_gato.imagen).apply(Utilidad.opcionesGlide(applicationContext))
            .transition(Utilidad.transicion).into(imagen)

        database_reference = FirebaseDatabase.getInstance().getReference()
        storage_reference = FirebaseStorage.getInstance().getReference()

        lista_gatos = Utilidad.obtenerListaGatos(database_reference)

        modificar.setOnClickListener {
            var localTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            fecha = localTime

            if (raza.text.toString().trim().isEmpty() ||
                    descripcion.text.toString().trim().isEmpty() ||
                    edad.text.toString().trim().isEmpty())

            {
                Toast.makeText(
                    applicationContext, "Faltan datos del formulario", Toast.LENGTH_SHORT
                ).show()
            } else{
                var url_imagen_firebase = String()
                launch {
                    if(url_gato == null){
                        url_imagen_firebase = pojo_gato.imagen!!
                    }else{
                        val url_imagen_firebase = Utilidad.guardarGato(storage_reference, pojo_gato.id!!, url_gato!!)
                    }

                    Utilidad.escribirGato(
                        database_reference, pojo_gato.id!!,
                        raza.text.toString().trim(),
                        descripcion.text.toString().trim(),
                        edad.text.toString().trim().toInt(),
                        calificacion.rating.toString().trim().toFloat(),
                        fecha,
                        url_imagen_firebase
                    )

                    Utilidad.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Gato modificado con exito"
                    )

                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }
            }


        }
        volver.setOnClickListener {
            val activity = Intent(applicationContext, VerGatos::class.java)
            startActivity(activity)
        }

        imagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }


    }
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri ->
        if(uri!=null){
            url_gato = uri
            imagen.setImageURI(uri)
        }


    }
    override val  coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


}