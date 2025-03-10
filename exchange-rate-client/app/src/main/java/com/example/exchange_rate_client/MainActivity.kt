package com.example.exchange_rate_client

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.exchange_rate_client.ui.theme.ExchangerateclientTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExchangerateclientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ExchangeRateClientApp()
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateClientApp() {
    val context = LocalContext.current // Obtener el contexto dentro de un @Composable
    var rate by remember { mutableStateOf("MXN") }
    var startDate by remember { mutableStateOf("1738368000000") }
    var endDate by remember { mutableStateOf("1743465600000") }
    var result by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cliente de Tasas de Cambio") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Campo de texto para la fecha de inicio
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Tasa de cambio (Código)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de texto para la fecha de inicio
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Fecha de inicio (timestamp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo de texto para la fecha de fin
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Fecha de fin (timestamp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón para ejecutar la consulta
                Button(
                    onClick = {
                        result = queryExchangeRates(
                            context,
                            startDate.toLong(),
                            endDate.toLong(),
                            rate
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Obtener tasas de cambio")
                }

                // Área para mostrar los resultados
                Text(
                    text = result,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@SuppressLint("Range")
private fun queryExchangeRates(
    context: Context,
    startDate: Long,
    endDate: Long,
    currencyCode: String // Nuevo parámetro para la divisa
): String {
    val uri = Uri.parse("content://com.example.exchangeRate.providers/exchange_rates_by_date_range")
    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        arrayOf(startDate.toString(), endDate.toString()),
        null
    )

    return cursor?.use {
        val results = StringBuilder()
        while (it.moveToNext()) {
            // Obtener el campo "rates" como un JSON (String)
            val ratesJson = it.getString(it.getColumnIndex("rates"))
            val lastUpdateUnix = it.getLong(it.getColumnIndex("last_update_unix"))
            val nextUpdateUnix = it.getLong(it.getColumnIndex("next_update_unix"))

            // Convertir el JSON a un Map<String, Double>
            val ratesMap = parseRatesJson(ratesJson)

            // Obtener el valor de la divisa específica
            val currencyRate = ratesMap[currencyCode]

            // Si la divisa existe, agregar los datos al resultado
            if (currencyRate != null) {
                results.append("Divisa: $currencyCode, Tasa: $currencyRate, Last Update: $lastUpdateUnix, Next Update: $nextUpdateUnix\n")
            }
        }

        // Devolver los resultados
        if (results.isEmpty()) {
            "No se encontraron datos para la divisa $currencyCode"
        } else {
            results.toString()
        }
    } ?: "No se encontraron datos"
}

// Función para convertir el JSON de "rates" a un Map<String, Double>
private fun parseRatesJson(ratesJson: String?): Map<String, Double> {
    if (ratesJson.isNullOrEmpty()) return emptyMap()

    return try {
        // Usar Gson para convertir el JSON a un Map
        val gson = Gson()
        val type = object : TypeToken<Map<String, Double>>() {}.type
        gson.fromJson(ratesJson, type)
    } catch (e: Exception) {
        Log.e("queryExchangeRates", "Error al parsear el JSON: ${e.message}")
        emptyMap()
    }
}


