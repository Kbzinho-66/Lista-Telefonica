package com.example.listatelefonica.model

import kotlinx.serialization.Serializable

/**
 * Classe modelo usada para guardar um Contato
 *
 * @property id Identificador numérico, gerado automaticamente
 * @property nome Nome do contato
 * @property telefone Telefone do contato, incluindo DDD
 * @property foto O uri da foto, convertido para string. Se for null,
 * é porque não foi associada uma imagem ao contato.
 * @property observacao Alguma observação sobre o contato
 *
 * @constructor Pode retornar um objeto com qualquer combinação de parâmetros passados
 */

@Serializable
data class Contato(var id: Int = 0,
                   val nome: String = "",
                   val telefone: String = "",
                   val foto: String? = null,
                   val observacao: String = "")


