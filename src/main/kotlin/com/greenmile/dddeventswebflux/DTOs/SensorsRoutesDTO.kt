package com.greenmile.dddeventswebflux.DTOs

import com.greenmile.dddeventswebflux.domain.SensorGPS

data class SensorsRoutesDTO(
        val coordinateSensorGPSVehicle: SensorGPS,
        val coordinateSensorGPSTelephone: SensorGPS,
) {
}