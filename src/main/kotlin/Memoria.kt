data class Memoria(
    var ocupado: Boolean=false,
    val capacidade: Int = 1000,
    var utilizando: Int = 0,

    ) {
    fun limpar() {
        this.utilizando = 0
        this.ocupado = false
    }

    fun alocar(memoria: Int) {
        if (this.utilizando + memoria > capacidade) {
            throw Exception()
        } else {
            this.utilizando += memoria
            this.ocupado = true
        }
    }
}