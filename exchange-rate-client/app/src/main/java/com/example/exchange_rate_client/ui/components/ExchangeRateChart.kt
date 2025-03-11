package com.example.exchange_rate_client.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExchangeRateChart(
    data: List<Pair<Long, Double>>,
    context: Context
) {

    if (data.isEmpty()) {
        Text("No hay datos para mostrar")
        return
    }

    // Convertir los datos al formato que espera MPAndroidChart
    val entries = data.mapIndexed { index, (date, rate) ->
        Entry(index.toFloat(), rate.toFloat())
    }

    // Calcular el rango del eje Y
    val minRate = data.minOf { it.second }.toFloat() // Valor mínimo de las tasas
    val maxRate = data.maxOf { it.second }.toFloat() // Valor máximo de las tasas

    // Crear el LineChart
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                // Configurar el eje X
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.valueFormatter = object : ValueFormatter() {
                    @SuppressLint("ConstantLocale")
                    private val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

                    override fun getFormattedValue(value: Float): String {
                        val date = data[value.toInt()].first
                        return dateFormat.format(Date(date))
                    }
                }

                xAxis.setLabelCount(5, true) // Mostrar 5 etiquetas en el eje X
                xAxis.textSize = 10f // Tamaño del texto de las etiquetas
                xAxis.setDrawLabels(true) // Asegurarse de que las etiquetas estén visibles

                // Agregar un título al eje X
                description.text = "Fecha y Hora" // Título del eje X
                description.textSize = 12f // Tamaño del título
                description.setPosition(500f, 50f) // Posición del título (ajusta según sea necesario)


                // Configurar el eje Y
                axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                axisLeft.textSize = 10f // Tamaño del texto de las etiquetas
                axisLeft.setLabelCount(5, true) // Mostrar 5 etiquetas en el eje Y
                axisLeft.setDrawLabels(true) // Asegurarse de que las etiquetas estén visibles
                axisRight.isEnabled = false

                // Establecer el rango del eje Y
                axisLeft.axisMinimum = minRate - 0.01f// Valor mínimo con un margen
                axisLeft.axisMaximum = maxRate + 0.01f// Valor máximo con un margen

                // Agregar un título al eje Y
                axisLeft.setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format(Locale.getDefault(), "%.2f", value) // Formatear los valores del eje Y
                    }
                })
                axisLeft.setDrawTopYLabelEntry(true) // Mostrar el título del eje Y

                // Crear el conjunto de datos
                val dataSet = LineDataSet(entries, "Tasa de cambio").apply {
                    color = android.graphics.Color.BLUE
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                }

                // Agregar los datos al gráfico
                this.data = LineData(dataSet)
                invalidate() // Refrescar el gráfico
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
    )
}