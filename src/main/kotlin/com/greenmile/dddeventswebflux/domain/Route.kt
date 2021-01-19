package com.greenmile.dddeventswebflux.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "route")
data class Route(
        @Id
        val id: Int,
        val vehicle: Vehicle,
        val employee: Employee,
        val stops: MutableList<Stop>,
        val name: String
) {
}