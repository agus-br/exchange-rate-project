package com.example.exchangeRate.network

import retrofit2.http.GET

interface ExchangeRateApiService {

    @GET("v6/e89744da2c7b056d1c7d4eae/latest/USD")
    suspend fun getLatestExchangeRates(): ExchangeRateResponse
}