package com.example.listatelefonica.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.listatelefonica.model.Contato

class DBSQLiteHelper(contexto: Context) :
    SQLiteOpenHelper(contexto, NOME_DATABASE, null, VERSAO_DATABASE) {

    companion object Nomes {
        const val VERSAO_DATABASE = 1
        const val NOME_DATABASE = "ContatosDB"
        const val TABELA_CONTATOS = "contatos"
        const val ID = "id"
        const val NOME = "nome"
        const val TELEFONE = "telefone"
        const val FOTO = "foto"
        const val OBSERVACAO = "observacao"
        val COLUNAS = arrayOf(ID, NOME, TELEFONE, FOTO, OBSERVACAO)
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val criarTabela = """
            CREATE TABLE contatos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT,
            telefone TEXT,
            foto TEXT,
            observacao TEXT)
        """.trimIndent()

        db?.execSQL(criarTabela)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS contatos")
        this.onCreate(db)
    }

    fun addContato(contato: Contato) {

        val db = writableDatabase
        val valores = ContentValues()

        with(valores) {
            put(NOME, contato.nome)
            put(TELEFONE, contato.telefone)
            put(FOTO, contato.foto)
            put(OBSERVACAO, contato.observacao)
        }.also {
            db.insert(TABELA_CONTATOS, null, valores)
            db.close()
        }
    }

    fun getContato(id: Int): Contato? {

        val db = readableDatabase
        val cursor = db.query(
            TABELA_CONTATOS, COLUNAS,
            "$ID = ?", arrayOf(id.toString()),
            null, null, null, null
        )

        return if (cursor == null) null
        else {
            cursor.moveToFirst()
            cursorToContato(cursor)
        }

    }

    private fun cursorToContato(cursor: Cursor): Contato {

        val id = cursor.getString(0).toInt()
        val nome = cursor.getString(1)
        val telefone = cursor.getString(2)
        val foto = cursor.getString(3)
        val observacao = cursor.getString(4)

        return Contato(id, nome, telefone, foto, observacao)
    }

    fun getAllContatos(): MutableList<Contato> {

        val listaContatos = mutableListOf<Contato>()
        val query = "SELECT * FROM $TABELA_CONTATOS ORDER BY $ID"
        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    listaContatos.add(cursorToContato(it))
                } while (it.moveToNext())
            }
        }

        return listaContatos
    }

    fun updateContato(contato: Contato): Int {

        val db = writableDatabase
        val valores = ContentValues()

        with(valores) {
            put(NOME, contato.nome)
            put(TELEFONE, contato.telefone)
            put(FOTO, contato.foto)
            put(OBSERVACAO, contato.observacao)
        }.also {
            val mudancas = db.update(
                TABELA_CONTATOS,
                valores,
                "$ID = ?",
                arrayOf(contato.id.toString())
            )

            db.close()
            return mudancas
        }
    }

    fun deleteContato(contato: Contato): Int {

        val db = writableDatabase
        val exclusoes = db.delete(
            TABELA_CONTATOS,
            "$ID = ?",
            arrayOf(contato.id.toString())
        )
        db.close()
        return exclusoes
    }
}