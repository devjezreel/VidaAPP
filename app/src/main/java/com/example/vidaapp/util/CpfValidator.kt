package com.example.vidaapp.util

/**
 * Validador de CPF único para o Vida APP.
 */
fun isCpfValid(cpf: String): Boolean {
    val cleanCpf = cpf.filter { it.isDigit() }
    if (cleanCpf.length != 11) return false
    if (cleanCpf.all { it == cleanCpf[0] }) return false

    fun calculateDigit(base: String): Int {
        var sum = 0
        var weight = base.length + 1
        for (char in base) {
            sum += (char - '0') * weight--
        }
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }

    return try {
        val digit1 = calculateDigit(cleanCpf.substring(0, 9))
        val digit2 = calculateDigit(cleanCpf.substring(0, 10))
        cleanCpf[9] - '0' == digit1 && cleanCpf[10] - '0' == digit2
    } catch (e: Exception) {
        false
    }
}
