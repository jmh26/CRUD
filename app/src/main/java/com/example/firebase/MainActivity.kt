package com.example.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract.Data
import android.provider.Settings
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var crear: Button
    private lateinit var ver: Button
    private lateinit var androidId: String
    private lateinit var db_refe: DatabaseReference
    private lateinit var generador: AtomicInteger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        crear = findViewById(R.id.Crear)
        ver = findViewById(R.id.Ver)


        ver.setOnClickListener {
            val activity = Intent(applicationContext, VerGatos::class.java)
            startActivity(activity)
        }

        crear.setOnClickListener {
            val activity = Intent(applicationContext, AnadirGato::class.java)
            startActivity(activity)
        }


        crearCanalNotificaciones()
        androidId = Settings.Secure.getString(contentResolver,Settings.Secure.ANDROID_ID)
        db_refe = FirebaseDatabase.getInstance().reference
        generador = AtomicInteger(0)



        //CONTROLADOR NOTIFICACIONES
        db_refe.child("Gatos").child("Datos")
            .addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_gato = snapshot.getValue(Gato::class.java)
                    if(!pojo_gato!!.user_notificacion.equals(androidId) && pojo_gato!!.estado_noti== Estado.creado){
                        db_refe.child("Gatos").child("Datos").child(pojo_gato.id!!).child("estado_noti").setValue(Estado.notificado)
                        generarNotificacion(generador.incrementAndGet(),pojo_gato, "Se ha creado el nuevo gato " + pojo_gato.nombre,"Nuevos datos en la app",
                            VerGatos::class.java)

                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_gato = snapshot.getValue(Gato::class.java)
                    if(!pojo_gato!!.user_notificacion.equals(androidId) && pojo_gato!!.estado_noti== Estado.modificado){
                        db_refe.child("Gatos").child("Datos").child(pojo_gato.id!!).child("estado_noti").setValue(Estado.modificado)
                        generarNotificacion(generador.incrementAndGet(),pojo_gato, "Se ha editado el gato "
                                + pojo_gato.nombre,"Datos modificados en la app",
                            EditarGato::class.java)

                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojo_gato = snapshot.getValue(Gato::class.java)
                    if(!pojo_gato!!.user_notificacion.equals(androidId)){
                        generarNotificacion(generador.incrementAndGet(),pojo_gato, "Se ha eliminado el gato "
                                + pojo_gato.nombre,"Datos eliminados en la app ",
                            VerGatos::class.java)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun generarNotificacion(id_noti: Int, pojo: Parcelable, contenido: String, titulo: String, destino: Class<*>){
        val id = "Canal de prueba"
        val actividad = Intent(applicationContext,destino)
        actividad.putExtra("nombre",pojo)

        val pendingIntent = PendingIntent.getActivity(this,0,actividad,PendingIntent.FLAG_IMMUTABLE)
        val notificacion = NotificationCompat.Builder(this,id).
        setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de informacion")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)){
            notify(id_noti,notificacion)
        }




    }


    private fun crearCanalNotificaciones(){
        val nombre = "canal basico"
        val id = "Canal de prueba"
        val descripcion = "Notificacion basica"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id,nombre,importancia).apply {
            description = descripcion
        }

        val nm: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)

    }


    override fun onBackPressed() {
        finish()
        val intent: Intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}