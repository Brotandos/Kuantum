package com.brotandos.kuantumlib

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL


fun BufferedInputStream.getString() : String {
    val bos = ByteArrayOutputStream()
    var i = this.read()
    while (i != -1) {
        bos.write(i)
        i = this.read()
    }
    return bos.toString()
}


interface LoadableApp {
    val baseUrl: String
    val defaultTimeout: Int
}


abstract class LoadableInteractor(baseUrl: String? = null,
                                  private val app: LoadableApp? = null) {

    /**
     * TODOs
     *
     * TODO add httpPost
     * TODO add httpPatch
     * TODO add httpDelete
     * */

    private val baseUrl = when {
        baseUrl != null -> baseUrl
        app != null -> app.baseUrl
        else -> ""
    }

    fun String.httpGet(onDataLoaded: (String) -> Unit,
                       onError: (java.lang.Exception) -> Unit,
                       loadingMarker: BooleanKuantum? = null,
                       connectionTimeout: Int? = null) {

        loadingMarker?.let { it becomes true }

        this@LoadableInteractor.doAsync {
            val url = URL(baseUrl + this@httpGet)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                val timeout = connectionTimeout ?: app?.defaultTimeout
                timeout?.let { connectTimeout = it }

                try {
                    val result = BufferedInputStream(inputStream).getString()
                    onComplete { onDataLoaded(result) }
                } catch (e: Exception) {
                    onError(e)
                } finally {
                    loadingMarker?.let { it becomes false }
                    disconnect()
                }
            }
        }
    }
}