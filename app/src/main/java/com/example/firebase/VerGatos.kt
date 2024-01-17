package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SearchView
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

        database_reference.child("Gatos").child("Razas").addValueEventListener(object : ValueEventListener{
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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_gatos,menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView


        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter((newText))
                return true
            }

        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                adaptador.filter.filter("")
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)

    }


}