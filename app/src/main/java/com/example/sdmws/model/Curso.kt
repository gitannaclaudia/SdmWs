package br.edu.ifsp.scl.sdm.pa2.sdmws.model

import com.google.gson.annotations.SerializedName

data class Curso(
    val horas: Int,
    val nome: String,
    val semestres: Int,
    val sigla: String
)