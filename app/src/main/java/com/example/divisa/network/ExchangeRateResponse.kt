package com.example.divisa.network

data class ExchangeRateResponse(
    val result: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>,
    val time_last_update_unix: Long,
    val time_next_update_unix: Long
)