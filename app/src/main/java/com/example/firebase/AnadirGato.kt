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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class AnadirGato : AppCompatActivity(), CoroutineScope {

    private lateinit var nom_raza: EditText
    private lateinit var descripcion: EditText
    private lateinit var edad: EditText
    private lateinit var imagen: ImageView
    private lateinit var calificacion: RatingBar
    private lateinit var fecha: String

    private lateinit var crear: Button
    private lateinit var volver: Button

    private lateinit var job: Job

    private var url_gato: Uri? = null
    private lateinit var database_reference: DatabaseReference
    private lateinit var storage_reference: StorageReference
    private lateinit var lista_gatos: MutableList<Gato>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anadir_gato)




        val this_activity = this


        job = Job()

        nom_raza = findViewById(R.id.raza)
        descripcion = findViewById(R.id.Descripcion)
        edad = findViewById(R.id.edad)
        imagen = findViewById(R.id.imagengato)
        calificacion = findViewById(R.id.rateargato)
        crear = findViewById(R.id.añadir)
        volver = findViewById(R.id.volver)


        database_reference = FirebaseDatabase.getInstance().getReference()
        storage_reference = FirebaseStorage.getInstance().getReference()
        lista_gatos = Utilidad.obtenerListaGatos(database_reference)







        crear.setOnClickListener {
            var localTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            fecha = localTime

            if (nom_raza.toString().trim().isEmpty() || descripcion.toString().trim()
                    .isEmpty() || calificacion.toString().trim().isEmpty()|| edad.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos del gato", Toast.LENGTH_SHORT
                ).show()
            } else if (url_gato == null) {
                Toast.makeText(
                    applicationContext, "Falta la foto del gato", Toast.LENGTH_SHORT
                ).show()


            } else if (Utilidad.existegato(lista_gatos, nom_raza.text.toString().trim())) {
                Toast.makeText(applicationContext, "Ese gato ya está en la bd", Toast.LENGTH_SHORT)
                    .show()

            } else {
                var id_gen: String? = database_reference.child("Gatos").child("Razas").push().key


                launch {
                    val url_gatos_firebase =
                        Utilidad.guardarGato(storage_reference, id_gen!!, url_gato!!)





                    Utilidad.escribirGato(
                        database_reference, id_gen!!,
                        nom_raza.text.toString().trim(),
                        descripcion.text.toString().trim(),
                        edad.text.toString().trim().toInt(),
                        calificacion.rating.toString().trim().toFloat(),
                        fecha,
                        url_gatos_firebase
                    )
                    Utilidad.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Gato añadido"
                    )
                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }

            }

        }

        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
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
    { uri: Uri ->
        if (uri != null) {
            url_gato = uri
            imagen.setImageURI(uri)
        }


    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}