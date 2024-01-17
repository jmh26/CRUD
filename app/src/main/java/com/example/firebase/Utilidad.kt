package com.example.firebase

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utilidad {
    companion object{
        fun existegato(gatos : List<Gato>, nom_raza: String): Boolean{
            return gatos.any { it.raza!!.lowercase()== nom_raza.lowercase() }
        }

        fun obtenerListaGatos(db_ref: DatabaseReference):MutableList<Gato>{

            var lista = mutableListOf<Gato>()

            db_ref.child("Gatos").child("Razas").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot){
                    snapshot.children.forEach{hijo: DataSnapshot ->
                        val pojo_raza = hijo.getValue(Gato::class.java)
                        lista.add(pojo_raza!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
            return lista
        }

        fun escribirGato(db_ref:DatabaseReference, id:String, raza:String, descripcion:String, edad:Int, calificacion:Float,fecha:String, url_firebase:String)=
            db_ref.child("Gatos").child("Razas").child(id).setValue(Gato(
                id,
                raza,
                descripcion,
                edad,
                calificacion,
                fecha,
                url_firebase
            ))

        suspend fun guardarGato(sto_ref: StorageReference, id:String, imagen: Uri):String{
            lateinit var url_gato_firebase: Uri

            url_gato_firebase=sto_ref.child("Gatos").child("Imagenes").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_gato_firebase.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.gato_defecto)
                .error(R.drawable.error)
            return options
        }

    }

}