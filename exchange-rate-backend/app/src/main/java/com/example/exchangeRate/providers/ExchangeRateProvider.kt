package com.example.exchangeRate.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.example.exchangeRate.ExchangeRateApplication
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI("com.example.exchangeRate.providers", "exchange_rates_by_date_range", 1)
}

class ExchangeRateProvider  : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    // Define un CoroutineScope para manejar las coroutines
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(arrayOf("rates", "last_update_unix", "next_update_unix"))
        val deferredResult = CompletableDeferred<Cursor?>()

        // Obtener los parámetros de la consulta
        val startDate = selectionArgs?.get(0)?.toLongOrNull()
            ?: throw IllegalArgumentException("Start date is required")
        val endDate = selectionArgs?.get(1)?.toLongOrNull()
            ?: throw IllegalArgumentException("End date is required")

        when (sUriMatcher.match(uri)) {
            1 -> scope.launch {
                try {
                    Log.d("ExchangeRateProvider", "Trying to get data")
                    val exchangeRates = (context as ExchangeRateApplication)
                        .container.exchangeRateRepository
                        .getExchangeRatesByDateRange(startDate, endDate)
                        .first()

                    // Agregar los datos al cursor
                    exchangeRates.forEach { exchangeRate ->
                        cursor.addRow(
                            arrayOf(
                                exchangeRate.rates.toString(), // Convertir el mapa a String
                                exchangeRate.lastUpdateUnix,
                                exchangeRate.nextUpdateUnix
                            )
                        )
                    }
                    Log.d("ExchangeRateProvider", "Data retrieved: ${cursor.count} rows")
                    deferredResult.complete(cursor)
                } catch (e: Exception) {
                    Log.e("ExchangeRateProvider", "Error retrieving exchange rates", e)
                    deferredResult.completeExceptionally(e)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        // Esperar el resultado de la coroutine
        return runBlocking {
            deferredResult.await()
        }
    }

    override fun getType(uri: Uri): String? {
        return when (sUriMatcher.match(uri)) {
            1 -> "vnd.android.cursor.dir/vnd.com.example.exchangeRate.providers.exchange_rates_by_date_range"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return  null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return  0
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return  0
    }
}