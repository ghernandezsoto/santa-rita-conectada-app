package cl.santaritaconectada.app.network.response

/**
 * Define la estructura de un set de datos para un gráfico.
 * (Ej: "Ingresos", "Egresos", "Mis Aportes")
 */
data class ChartDataset(
    val label: String,
    val data: List<Float>
)

/**
 * Define la estructura completa de la respuesta JSON para un gráfico,
 * combinando las etiquetas (ej: "Enero", "Febrero") con los sets de datos.
 */
data class ChartData(
    val labels: List<String>,
    val datasets: List<ChartDataset>
)