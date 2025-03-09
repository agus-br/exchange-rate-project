package com.example.exchangeRate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.exchangeRate.ui.ExchangeRateApp
import com.example.exchangeRate.ui.theme.DivisaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DivisaTheme {
                ExchangeRateApp()
            }
        }
    }
}
