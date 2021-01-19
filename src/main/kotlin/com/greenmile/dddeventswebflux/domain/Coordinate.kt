package com.luis.avaliacao.domain

import java.util.*

data class Coordinate (
        val id: Int,
        var latitude: Double,
        var longitude: Double,
        val datePing: Date,
        )