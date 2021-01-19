package com.greenmile.dddeventswebflux.DTOs

import com.luis.avaliacao.domain.Coordinate

data class NotificationDTO(
        val routeId: Int,
        val lastCoordinateSensorGPSFromVehicle: Coordinate?,
        val actualCoordinateSensorGPSFromVehicle: Coordinate?,
        val lastCoordinateSensorGPSFromTelephone: Coordinate?,
        val actualCoordinateSensorGPSFromTelephone: Coordinate?
){
}