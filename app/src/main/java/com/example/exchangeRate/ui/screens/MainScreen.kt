package com.example.exchangeRate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.toList

@Composable
fun MainScreen(
    viewModel: ExchangeRateViewModel
) {
    // Observa los datos del ViewModel
    val exchangeRates = viewModel.conversionRates.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Tasas de cambio en USD",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Indicador de carga
        if (isLoading) {
            CircularProgressIndicator()
        }

        // Mensaje de error
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Lista de tasas de conversión
        if (exchangeRates.isNotEmpty()) { // Verifica si el mapa no está vacío
            LazyColumn {
                items(exchangeRates.toList()) { (currency, rate) -> // Usa toList() en el mapa
                    Text(
                        text = "$currency: $rate",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        } else {
            Text(
                text = "No hay datos disponibles",
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}