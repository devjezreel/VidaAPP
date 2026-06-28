package com.example.vidaapp.util

fun isValidCpf(cpf: String): Boolean {
    if (cpf.length != 11) return false
    if (cpf.all { it == cpf[0] }) return false
    
    fun calculateDigit(base: String): Int {
        var sum = 0
        var weight = base.length + 1
        for (char in base) {
            sum += (char - '0') * weight--
        }
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
    
    val digit1 = calculateDigit(cpf.substring(0, 9))
    val digit2 = calculateDigit(cpf.substring(0, 10))
    
    return cpf[9] - '0' == digit1 && cpf[10] - '0' == digit2
}
