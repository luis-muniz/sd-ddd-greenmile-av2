package com.greenmile.dddeventswebflux.repositories

import com.greenmile.dddeventswebflux.domain.SensorGPS
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface SensorGPSRepository: ReactiveMongoRepository<SensorGPS, Int>