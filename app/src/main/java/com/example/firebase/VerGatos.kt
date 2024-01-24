package com.example.firebase

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerGatos : AppCompatActivity() {

    private lateinit var volver: Button

    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Gato>
    private lateinit var adaptador: GatoAdapter
    private lateinit var database_reference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_gato)

        volver = findViewById(R.id.volver_inicio)

        lista = mutableListOf()
        database_reference = FirebaseDatabase.getInstance().getReference()

        database_reference.child("Gatos").child("Datos").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                lista.clear()
                snapshot.children.forEach{hijo : DataSnapshot?
                    ->
                    val pojo_gato = hijo?.getValue(Gato::class.java)
                    lista.add(pojo_gato!!)
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        adaptador = GatoAdapter(lista)
        recycler = findViewById(R.id.lista_gatos)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)

        }

        val imageViewFiltrar: ImageView = findViewById(R.id.embudo)
        imageViewFiltrar.setOnClickListener{
                view ->
            showFiltrarMenu(view)
        }




        val searchView = findViewById<SearchView>(R.id.Busqueda)


        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter((newText))
                return true
            }

        })

    }

    private fun showFiltrarMenu(view: View){
        val popupMenu = PopupMenu(this,view)
        popupMenu.menuInflater.inflate(R.menu.menu_gatos, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId){
                R.id.porEdad -> {
                    adaptador.ordenarPorEdad()
                    true
                }
                R.id.porNombre -> {
                    adaptador.ordenarPorNombre()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }



}