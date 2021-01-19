package com.greenmile.dddeventswebflux.domain

import com.greenmile.dddeventswebflux.domain.SensorGPS
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "vehicle")
data class Vehicle (
    @Id
    val id: Int,
    val sensorGPS: SensorGPS,
    val brand: String,
    val model: String,
    val plate: String,
        ){
}