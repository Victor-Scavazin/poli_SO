data class Log(
    val name: String,
    val tipo: EventType,
    val chegada: Int? = null,
    val saida: Int? = null,
    val reacao: Reacao? = null,
    val memoria: Int? = null,
)
enum class Reacao {
    EXITO, FALHA, INTERRUPCAO, SEM_ROTINA_ASSOCIADA
}

fun MutableList<Log>.createLog(
    evento: Evento,
    chegada: Int? = null,
    saida: Int? = null,
    reacao: Reacao? = null,
    memoria: Int? = null,
) = this.add(
    Log(
        name = evento.name,
        tipo = evento.tipo,
        chegada = chegada,
        saida = saida,
        reacao = reacao,
        memoria = memoria,
    )
)
