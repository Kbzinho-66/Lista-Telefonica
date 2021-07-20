package com.example.listatelefonica.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listatelefonica.databinding.ContatoLayoutBinding
import com.example.listatelefonica.model.Contato

/**
 * Classe que adapta os objetos [com.example.listatelefonica.model.Contato]
 * para serem exibidos na Recycler View
 */

class AdapterContato(private val contatos: MutableList<Contato>, val contexto: Context) :
    RecyclerView.Adapter<AdapterContato.ContatoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoHolder {

        val layoutBinding = ContatoLayoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ContatoHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: ContatoHolder, position: Int) {
        val contato: Contato = contatos[position]

        with(holder) {
            setContato(contato)
            itemView.setOnClickListener {
                val intentMostrarContato =
                    Intent(holder.itemView.context, MostrarContato::class.java)
                intentMostrarContato.putExtra("ID", contatos[position].id)
                contexto.startActivity(intentMostrarContato)
            }
        }
    }

    override fun getItemCount(): Int = contatos.size

    inner class ContatoHolder(private val layoutBinding: ContatoLayoutBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {

        fun setContato(contato: Contato) {
            with(layoutBinding) {
                contato.foto?.let { Glide.with(contexto).load(it).into(imagemContatoRv) }
                nomeContatoRv.text = contato.nome
                observacaoContatoRv.text = contato.observacao
            }
        }
    }

    fun codigoContato(adapterPosition: Int): Int {
        return contatos[adapterPosition].id
    }
}