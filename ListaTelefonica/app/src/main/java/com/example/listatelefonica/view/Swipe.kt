package com.example.listatelefonica.view

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.listatelefonica.R
import com.example.listatelefonica.dao.DBSQLiteHelper

abstract class Swipe(
    private val contexto: Context,
    private val adapterContato: AdapterContato,
    private val db: DBSQLiteHelper
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    /**
     * Função que implementa o swipe de cada item da RecyclerView.
     * Baseada fortemente desse código:
     * https://github.com/kitek/android-rv-swipe-delete/blob/master/app/src/main/java/pl/kitek/rvswipetodelete/SwipeToDeleteCallback.kt
     *
     * @param contexto Contexto necessário somente pra procurar recursos e iniciar activities
     * @param adapterContato O mesmo adapter sendo usado pelo resto da aplicação, necessário para
     * saber com que contato chamar a [com.example.listatelefonica.view.MostrarContato]
     * @param db O [DBSQLiteHelper] já criado na MainActivity. Poderia ser criado novamente nessa classe,
     * mas como seria criado com o contexto da Main, não ia mudar nada.
     */

    private val fundo = ColorDrawable()
    private val corLimpar = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private val iconeEdicao = Icone(ContextCompat.getDrawable(contexto, R.drawable.ic_edit))
    private val iconeChamada = Icone(ContextCompat.getDrawable(contexto, R.drawable.ic_call))

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val alturaItem = itemView.bottom - itemView.top
        val isCancelled = dX == 0f && !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(
                c, itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Desenhar o fundo para as duas funções
        with(fundo) {
            color = ContextCompat.getColor(contexto, R.color.Slate_Blue)
            setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
            draw(c)
        }

        // Calcular a posição dos dois ícones
        with(iconeEdicao) {
            topo = itemView.top + (alturaItem - alturaIntrinsica!!) / 2
            margem = (alturaItem - alturaIntrinsica) / 2
            esquerda = itemView.right - margem - larguraIntrinsica!!
            direita = itemView.right - margem
            baixo = topo + alturaIntrinsica
            icone?.run {
                setBounds(esquerda, topo, direita, baixo)
                draw(c)
            }
        }

        with(iconeChamada) {
            topo = itemView.top + (alturaItem - alturaIntrinsica!!) / 2
            margem = (alturaItem - alturaIntrinsica) / 2
            esquerda = itemView.left + margem
            direita = itemView.left + margem + alturaIntrinsica
            baixo = topo + alturaIntrinsica
            icone?.run {
                setBounds(esquerda, topo, direita, baixo)
                draw(c)
            }
        }

        // Desenhar os ícones
        with(iconeEdicao) {
            icone?.run {
                setBounds(esquerda, topo, direita, baixo)
                draw(c)
            }
        }

        with(iconeChamada) {
            icone?.run {
                setBounds(esquerda, topo, direita, baixo)
                draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        /**
         * Método que, de acordo com a direção do swipe, busca um contato e chama
         * o método adequado.
         */

        val id = adapterContato.codigoContato(viewHolder.adapterPosition)

        if (direction == ItemTouchHelper.LEFT) {
            val intentEditarContato = Intent(contexto, MostrarContato::class.java)
            intentEditarContato.putExtra("ID", id)
            contexto.startActivity(intentEditarContato)
            adapterContato.notifyItemChanged(viewHolder.adapterPosition)
        } else {
            val intentChamada = Intent(Intent.ACTION_DIAL)
            val contato = db.getContato(id)
            intentChamada.data = Uri.parse("tel: ${contato?.telefone}")
            contexto.startActivity(intentChamada)
            adapterContato.notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, corLimpar)
    }

    private class Icone(val icone: Drawable?) {
        /**
         * Pequena classe criada só pra reduzir a duplicação de código
         */

        val alturaIntrinsica = icone?.intrinsicHeight
        val larguraIntrinsica = icone?.intrinsicWidth
        var topo: Int = 0
        var margem: Int = 0
        var esquerda: Int = 0
        var direita: Int = 0
        var baixo: Int = 0
    }
}

