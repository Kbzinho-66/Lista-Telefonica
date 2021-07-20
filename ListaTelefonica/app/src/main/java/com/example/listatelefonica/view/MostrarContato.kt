package com.example.listatelefonica.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.listatelefonica.R
import com.example.listatelefonica.dao.DBSQLiteHelper
import com.example.listatelefonica.databinding.ActivityMostrarContatoBinding
import com.example.listatelefonica.model.Contato

class MostrarContato : AppCompatActivity() {

    private val binding: ActivityMostrarContatoBinding by lazy {
        ActivityMostrarContatoBinding.inflate(
            layoutInflater
        )
    }
    private val db: DBSQLiteHelper by lazy { DBSQLiteHelper(this) }

    private var caminho: String? = null
    private val buscarGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                caminho = it.toString()
                Glide.with(this).load(caminho).into(binding.fotoContatoAtual)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = intent.getIntExtra("ID", -1)
        if (id == -1) {
            return
        }

        val contato = db.getContato(id)

        if (contato != null) {
            with(binding) {

                // Atualizar os campos de dados
                Glide.with(this@MostrarContato).load(contato.foto).into(fotoContatoAtual)
                (nomeContatoAtual as TextView).text = contato.nome
                (telefoneContatoAtual as TextView).text = contato.telefone
                (observacaoContatoAtual as TextView).text = contato.observacao

            }
        }

        contato?.let {
            with(binding) {
                // Inicializar os a mudança de foto + os 3 botões
                fotoContatoAtual.setOnClickListener {
                    buscarGaleria.launch("image/*")
                }

                botaoAlterarContato.setOnClickListener {
                    db.updateContato(
                        Contato(
                            id = contato.id,
                            nome = nomeContatoAtual.text.toString(),
                            telefone = telefoneContatoAtual.text.toString(),
                            foto = caminho ?: contato.foto,
                            observacao = observacaoContatoAtual.text.toString()
                        )
                    )

                    startActivity(Intent(baseContext, MainActivity::class.java))
                }

                botaoRemoverContato.setOnClickListener {
                    AlertDialog.Builder(botaoRemoverContato.context)
                        .setTitle(R.string.confirm_deletion)
                        .setMessage(R.string.are_you_sure)
                        .setIcon(R.drawable.ic_delete)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            val contatoPraDeletar = Contato()
                            contatoPraDeletar.id = id
                            db.deleteContato(contatoPraDeletar)
                            startActivity(Intent(baseContext, MainActivity::class.java))
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()
                }

                botaoLigar.setOnClickListener {
                    val intentChamada = Intent(Intent.ACTION_DIAL)
                    intentChamada.data = Uri.parse("tel: ${contato.telefone}")
                    startActivity(intentChamada)
                }
            }
        }
    }
}