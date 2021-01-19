package com.greenmile.dddeventswebflux.domain

import com.luis.avaliacao.domain.Coordinate
import java.util.*

data class Stop (
        val id: Int,
        val coordinate: Coordinate,
        val address: String,
        val arrivalAt: Date? = null,
        val departureAt: Date? = null,
        ){
}