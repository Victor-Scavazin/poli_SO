data class Evento(
    val name: String,
    var chegada: Int?=null,
    val tempo: Int,
    val memoria: Int,
    val tipo: EventType,
    var rotinaAssocidada: Rotina? = null,
)

