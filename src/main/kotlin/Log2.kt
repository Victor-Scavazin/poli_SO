fun createLog(
    name: String,
    instante: Int,
    tipo: EventType,
    processo: String
) {
    println("==========================================")
    println("Evento ${name}")
    println("Tipo: ${tipo}")
    println("Instante: ${instante} segundos")
    println(processo)
}