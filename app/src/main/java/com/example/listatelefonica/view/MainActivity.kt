package com.example.listatelefonica.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listatelefonica.dao.DBSQLiteHelper
import com.example.listatelefonica.databinding.ActivityMainBinding
import com.example.listatelefonica.model.Contato

open class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val db: DBSQLiteHelper by lazy { DBSQLiteHelper(this) }
    private val listaContatos: MutableList<Contato> by lazy { db.getAllContatos() }

    private var pediuPermissao: Boolean = false
    private val pedirPermissao =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissaoDada ->
            if (permissaoDada)
                Toast
                    .makeText(
                        this,
                        "Permissão para acessar os arquivos concedida.",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            else {
                mostraAlerta(
                    Manifest.permission.READ_EXTERNAL_STORAGE, "Leitura de Armazenamento Externo"
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if (!pediuPermissao) {
            pedirPermissao.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            pediuPermissao = true
        }

        inicializarRecyclerView()

        binding.fabAdicionar.setOnClickListener {
            val intentAdicionarContato = Intent(this, NovoContato::class.java)
            startActivity(intentAdicionarContato)
        }

        /**
         * Frescurinhas pra fazer ainda
         * TODO (Auto formatar o número de telefone)
         */
    }

    private fun inicializarRecyclerView() {
        /**
         * Definir o layout, orientação e adapter da Recycler View
         */
        val layoutManager = LinearLayoutManager(this).also {
            it.orientation = LinearLayoutManager.VERTICAL
        }
        binding.rvContatos.apply {
            setLayoutManager(layoutManager)
            val adapterContato = AdapterContato(listaContatos, this@MainActivity)
            adapter = adapterContato.also {
                it.notifyDataSetChanged()
            }

            val swipeHandler = object : Swipe(this@MainActivity, adapterContato, db) {}
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(binding.rvContatos)
        }

    }

    private fun mostraAlerta(permissao: String, nome: String) {
        /**
         * Mostrar um alerta quando uma permissão é negada, além de avisar
         * sobre a necessidade da permissão e gerar o prompt para pedir de novo.
         *
         * @param permissao A permissao que foi negada, de forma a lançar a requisição de novo
         * com esse parâmetro
         *
         * @param nome O nome que será usado para identificar a permissão na mensagem do alerta.
         */

        AlertDialog.Builder(this)
            .apply {
                setMessage("Você precisa conceder permissão de $nome para poder adicionar fotos aos contatos.")
                setTitle("Permissão Necessária")
                setPositiveButton("OK") { _, _ ->
                    pedirPermissao.launch(permissao)
                }
            }
            .create()
            .show()

    }
}
