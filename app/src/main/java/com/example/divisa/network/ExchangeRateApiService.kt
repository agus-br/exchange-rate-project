package com.example.divisa.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApiService {

    @GET("v6/e89744da2c7b056d1c7d4eae/latest/USD")
    suspend fun getLatestExchangeRates(): ExchangeRateResponse
}