package com.example.listatelefonica.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.listatelefonica.dao.DBSQLiteHelper
import com.example.listatelefonica.databinding.ActivityAdicionarContatoBinding
import com.example.listatelefonica.model.Contato

class NovoContato : AppCompatActivity() {

    private val binding: ActivityAdicionarContatoBinding by lazy {
        ActivityAdicionarContatoBinding.inflate(layoutInflater)
    }
    private val db: DBSQLiteHelper by lazy { DBSQLiteHelper(this) }
    private var caminho: String? = null

    private val buscarGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                caminho = uri.toString()
                Glide.with(this).load(caminho).into(binding.fotoNovoContato)
                binding.fotoNovoContato.foreground = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            fotoNovoContato.setOnClickListener { buscarGaleria.launch("image/*") }

            botaoConfirmarAdicao.setOnClickListener {

                db.addContato(
                    Contato(
                        nome = binding.nomeNovoContato.text.toString(),
                        telefone = binding.telefoneNovoContato.text.toString(),
                        foto = caminho,
                        observacao = binding.observacaoNovoContato.text.toString()
                    )
                )

                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

    }

}