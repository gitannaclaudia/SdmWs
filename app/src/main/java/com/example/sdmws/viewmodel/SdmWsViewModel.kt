package com.example.sdmws.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import br.edu.ifsp.scl.sdm.pa2.sdmws.model.Curso
import br.edu.ifsp.scl.sdm.pa2.sdmws.model.Disciplina
import br.edu.ifsp.scl.sdm.pa2.sdmws.model.Semestre
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class SdmWsViewModel(application: Application) : AndroidViewModel(application) {
    val cursoMl: MutableLiveData<Curso> = MutableLiveData()
    val semestreMl: MutableLiveData<Semestre> = MutableLiveData()
    val disciplinaMl: MutableLiveData<Disciplina> = MutableLiveData()
    private val escopoCorrotinas = CoroutineScope(Dispatchers.IO + Job())
    private val filaRequisicoesVolley: RequestQueue =
        Volley.newRequestQueue(application.baseContext)

    companion object {
        val URL_BASE = "http://mobile.pro.br/sdm_ws"
        val ENDPOINT_CURSO = "/curso"
        val ENDPOINT_DISCIPLINA = "/disciplina"
        val ENDPOINT_SEMESTRE = "/semestre"
    }


    fun getCurso() {
        escopoCorrotinas.launch {
            val urlCurso = "${URL_BASE}${ENDPOINT_CURSO}"
            val requisicaoCursoJob = JsonObjectRequest(
                Request.Method.GET,
                urlCurso,
                null,
                { response ->
                    if (response != null) {
                        val curso: Curso = jsonToCurso(response)
                        cursoMl.postValue(curso)
                    }
                },
                { error -> Log.e(urlCurso, error.toString()) }
            )
            filaRequisicoesVolley.add(requisicaoCursoJob)
        }

    }

    fun getSemestre(sid: Int) {
        escopoCorrotinas.launch {
            val urlSemestre = "${URL_BASE}${ENDPOINT_SEMESTRE}"
            val requisicaoSemestreJob = JsonArrayRequest(
                Request.Method.GET,
                urlSemestre,
                null,
                { response ->
                    response?.also { disciplinaJar ->
                        val semestre = Semestre()
                        for (indice in 0 until disciplinaJar.length()){
                            val disciplinaJson = disciplinaJar.getJSONObject(indice)
                            val disciplina = jsonToDisciplina(disciplinaJson)
                            semestre.add(disciplina)
                        }
                        semestreMl.postValue(semestre)
                    }
                },
                { error -> Log.e(urlSemestre, error.toString()) }
            )
            filaRequisicoesVolley.add(requisicaoSemestreJob)
        }
    }

    fun getDisciplina(sigla: String) {

    }

    private fun jsonToCurso(json: JSONObject): Curso {
        val curso: Curso = Curso(
            json.getInt("horas"),
            json.getString("nome"),
            json.getInt("semestres"),
            json.getString("sigla")
        )

        return curso
    }

    private fun jsonToDisciplina(json: JSONObject): Disciplina {
        val disciplina: Disciplina = Disciplina(
            json.getInt("aulas"),
            json.getInt("horas"),
            json.getString("nome"),
            json.getString("sigla"),
        )

        return disciplina
    }
}