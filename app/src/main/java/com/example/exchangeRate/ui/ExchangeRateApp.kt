package com.example.exchangeRate.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exchangeRate.data.ExchangeRateRepository
import com.example.exchangeRate.ui.screens.ExchangeRateViewModel
import com.example.exchangeRate.ui.screens.MainScreen

@Composable
fun ExchangeRateApp(
    repository: ExchangeRateRepository
) {
    // Inicializa el ViewModel usando la fábrica (Factory)
    val exchangeRateViewModel: ExchangeRateViewModel =
        viewModel(factory = ExchangeRateViewModel.Factory)

    // Cargar los datos cuando se inicia la aplicación
    LaunchedEffect(Unit) {
        exchangeRateViewModel.loadExchangeRates()
    }

    // Mostrar la pantalla principal
    MainScreen(viewModel = exchangeRateViewModel)
}