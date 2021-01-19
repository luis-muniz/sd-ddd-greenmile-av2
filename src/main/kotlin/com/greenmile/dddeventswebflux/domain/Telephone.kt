package com.greenmile.dddeventswebflux.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "telephone")
data class Telephone (
        @Id
        val id: Int,
        val sensorGPS: SensorGPS,
        var lastTimeCoordinateAwayFromVehicle: Date? = null,
        val brand: String,
        val model: String,
        val imei: String,
        ){
}