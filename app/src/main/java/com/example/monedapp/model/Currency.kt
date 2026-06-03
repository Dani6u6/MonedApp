package com.example.monedapp.model

data class Currency(
    val code: String,
    val name: String,
    val factor: Double // Valor relativo a una moneda base (ej. USD)
) {
    override fun toString(): String = "$code - $name"
}
