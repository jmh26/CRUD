package com.example.firebase

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GatoAdapter(private val lista_gato: MutableList<Gato>):
    RecyclerView.Adapter<GatoAdapter.GatoViewHolder>(), Filterable {
    private lateinit var contexto: Context
    private var lista_filter = lista_gato

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GatoAdapter.GatoViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_gato,parent,false)
        contexto = parent.context
        return GatoViewHolder(vista_item)
    }


    override fun onBindViewHolder(holder: GatoAdapter.GatoViewHolder, position: Int){
        val item_actual = lista_filter[position]
        holder.nombre.text = item_actual.nombre
        holder.descripcion.text = item_actual.descripcion
        holder.edad.text = item_actual.edad.toString() + " años"
        holder.calificacion.rating = item_actual.calificacion?.toFloat() ?: 0.0f
        holder.fecha.text = item_actual.fecha






        val URL:String? = when(item_actual.imagen){
            ""-> null
            else -> item_actual.imagen
        }

        Glide.with(contexto).load(URL).apply(Utilidad.opcionesGlide(contexto))
            .transition(Utilidad.transicion).into(holder.miniatura)


        holder.editar.setOnClickListener {
            val activity = Intent(contexto,EditarGato::class.java)
            activity.putExtra("nombre", item_actual)
            contexto.startActivity(activity)
        }

        holder.eliminar.setOnClickListener {
            val database_reference = FirebaseDatabase.getInstance().getReference()
            val storage_reference = FirebaseStorage.getInstance().getReference()

            lista_filter.remove(item_actual)

            val androidId = Settings.Secure.getString(contexto.contentResolver, Settings.Secure.ANDROID_ID)


            storage_reference.child("Gatos").child("Imagenes").child(item_actual.id!!).delete()

            database_reference.child("Gatos").child("Datos").child(item_actual.id!!).child("user_notificacion").setValue(androidId)
            database_reference.child("Gatos").child("Datos").child(item_actual.id!!).removeValue()

            Toast.makeText(contexto, "Gato borrado", Toast.LENGTH_SHORT).show()



        }

    }

    fun ordenarPorEdad(){
        lista_gato.sortBy { it.edad }
        notifyDataSetChanged()
    }
    fun ordenarPorNombre(){
        lista_gato.sortBy { it.nombre }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = lista_filter.size

    class GatoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val descripcion: TextView = itemView.findViewById(R.id.item_descripcion)
        val edad: TextView = itemView.findViewById(R.id.item_edad)
        val calificacion: RatingBar = itemView.findViewById(R.id.item_rate)
        val fecha: TextView = itemView.findViewById(R.id.item_fecha)
        val editar: ImageView = itemView.findViewById(R.id.item_editar)
        val eliminar: ImageView = itemView.findViewById(R.id.item_borrar)
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if(busqueda.isEmpty()){
                    lista_filter = lista_gato
                } else{
                    lista_filter = (lista_gato.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Gato>
                }
                val filterResults = FilterResults()
                filterResults.values = lista_filter
                return filterResults

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

}



