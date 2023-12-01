fun main() {
    var estado = Estado.SUBMETER.ordem
    val memoria1 = Memoria()
    val processador1 = Processador()
    val rotina0 = Rotina()
    val rotinaDependencia = Rotina(null, Evento("evento X", null, 90, 555, EventType.X, rotina0))
    val rotinaInterrupcao = Rotina(50)
    val logs = mutableListOf<Log>()
    val eventoA = Evento(
        "evento A",
        2,
        5,
        400,
        EventType.A,
        rotinaInterrupcao
    )
    val eventoC = Evento(
        "evento C",
        50,
        75,
        400,
        EventType.C,
        rotinaDependencia
    )
    val eventoB = Evento(
        "evento B",
        20,
        80,
        200,
        EventType.B,
        rotina0
    )
    val eventoD = Evento(
        "evento D",
        95,
        180,
        800,
        EventType.D,
    )
    val eventoE = Evento(
        "evento D",
        76,
        122,
        800,
        EventType.C,
        rotinaInterrupcao
    )
    val eventoF = Evento(
        "evento F",
        50,
        75,
        400,
        EventType.C,
        rotinaDependencia
    )
    val eventoG = Evento(
        "evento G",
        50,
        75,
        400,
        EventType.C,
        rotinaDependencia
    )
    val eventoI_O = Evento(
        "evento I/O",
        50,
        75,
        400,
        EventType.A,
        rotina0,
        Device("Mouse", "Entrada")
    )
    val eventoI_O2 = Evento(
        "evento I/O 2",
        50,
        75,
        400,
        EventType.A,
        rotina0,
        Device("Impressora", "Saída")
    )
    val listaDeEventos2 = listOf(eventoD, eventoB, eventoC, eventoA, eventoE ,eventoF,eventoI_O,eventoI_O2 )
    val listaDeEventos = listOf(eventoA)
    val listaDeEventos_io = listOf(eventoB, eventoI_O, eventoI_O2, eventoD)
    val lEOrdenada = listaDeEventos2.sortedBy { it.chegada }.toMutableList()

    println(lEOrdenada)
    val processoEmExecucao = mutableListOf<Evento>()
    var instante: Int = lEOrdenada[0].chegada!!

    while (true) {
        if (estado == 1) {
            if (lEOrdenada[0].chegada!! > instante) {
                instante = lEOrdenada[0].chegada!!
            }
            createLog(lEOrdenada[0].name, instante, lEOrdenada[0].tipo, "Chegada e ingresso no sistema")
            processoEmExecucao.add(lEOrdenada[0])
            lEOrdenada.removeAt(0)
            logs.createLog(processoEmExecucao[0], chegada = instante)
            estado = 2

        } else if (estado == 2) {
            if (!memoria1.ocupado) {
                try {
                    memoria1.alocar(processoEmExecucao.first().memoria)
                    createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Alocação de memória")
                    estado = 3
                } catch (e: Exception) {
                    createLog(
                        processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo,
                        "Memoria insuficiente"
                    )
                    logs.createLog(processoEmExecucao[0], saida = instante, reacao = Reacao.FALHA)
                    proximoEvento(lEOrdenada, processoEmExecucao)?.let { estado = 1 } ?: break
                }
            }
        } else if (estado == 3) {
            if (!processador1.ocupado) {
                processador1.ocupado = true
                createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Alocação de processador")
                processoEmExecucao.first().io?.let {
                    it.lock = true
                    createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Travamento de dispositivo")
                }
                estado = 4
            }
        } else if (estado == 4) {
            processoEmExecucao[0].rotinaAssocidada?.let { rotina ->
                if (rotina == rotinaInterrupcao) {
                    estado = 7
                } else {
                    instante += processoEmExecucao[0].tempo

                    if (rotina.evento != null) {
                        lEOrdenada.add(
                            rotina.evento.apply { chegada = processoEmExecucao[0].chegada!! }
                        )
                        lEOrdenada.sortBy { it.chegada }
                        createLog(
                            processoEmExecucao[0].name,
                            instante,
                            EventType.CRIACAO_EVENTO,
                            "Criação de evento dependente"
                        )
                    }

                    processador1.ocupado = false
                    createLog(
                        processoEmExecucao[0].name,
                        instante,
                        processoEmExecucao[0].tipo,
                        "Liberação de processador"
                    )
                    processoEmExecucao.first().io?.let {
                        it.lock = false
                        createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Destravamento do dispositivo")

                    }
                    estado = 5
                    logs.createLog(processoEmExecucao[0], saida = instante, reacao = Reacao.EXITO)
                }
            } ?: proximoEvento(lEOrdenada, processoEmExecucao, true, logs)?.let { estado = 1 } ?: break
//                processador1.ocupado = false
//                createLog(
//                    processoEmExecucao[0].name,
//                    instante,
//                    processoEmExecucao[0].tipo,
//                    "Liberação de processador"
//                )
//                memoria1.limpar()
//                createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Liberação de Memória")
//

        } else if (estado == 5) {
            memoria1.limpar()
            createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Liberação de Memória")
            estado = 6

        } else if (estado == 6) {
            createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Conclusão do evento")
            proximoEvento(lEOrdenada, processoEmExecucao)?.let { estado = 1 } ?: break
        } else if (estado == 7) {
            createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Interrupção")
            logs.createLog(
                processoEmExecucao[0],
                chegada = instante,
                saida = instante + rotinaInterrupcao.tempo!!,
                reacao = Reacao.INTERRUPCAO
            )
            instante += rotinaInterrupcao.tempo
            val random = (0..1).random()
            print("+++++++++++++++++++++++++++ RAMDOM:$random +++++++++++++++++++++++++++")
            if (random == 0) {
                println("Interrupção aceita realizada, reiniciando o processo")
                processoEmExecucao[0].rotinaAssocidada = rotina0
                estado = 4
            } else {
                println("Interrupção não aceita, processo finalizado")
                proximoEvento(lEOrdenada, processoEmExecucao, interrupcao = true)?.let {

                    processador1.ocupado = false
                    createLog(
                        processoEmExecucao[0].name,
                        instante,
                        processoEmExecucao[0].tipo,
                        "Liberação de processador"
                    )
                    logs.createLog(processoEmExecucao[0], saida = instante, reacao = Reacao.FALHA)
                    memoria1.limpar()
                    createLog(processoEmExecucao[0].name, instante, processoEmExecucao[0].tipo, "Liberação de Memória")
                    estado = 6
                } ?: break
            }
        }
    }
    println("====================================================================================")
    println("====================================================================================")
    println("                               LOGS DE EXECUÇÃO                                     ")
    println("====================================================================================")
    println("====================================================================================")
    logs.forEach {
        println(it)
    }

}

fun proximoEvento(
    listaDeEventosOrdenada: MutableList<Evento>,
    processoEmExecucao: MutableList<Evento>,
    semRotina: Boolean = false,
    logs: MutableList<Log> = mutableListOf(),
    interrupcao: Boolean = false
): Int? {
    if (semRotina) {
        println("Processo ${processoEmExecucao[0].name} não possui rotina associada")
        logs.createLog(processoEmExecucao[0], reacao = Reacao.SEM_ROTINA_ASSOCIADA)

    }
    return if (listaDeEventosOrdenada.isEmpty()) {
        println("Lista de eventos vazia")
        null
    } else {
        if (!interrupcao) {
            println("Buscando proximo evento...")
            processoEmExecucao.removeAt(0)
        }
        1
    }
}