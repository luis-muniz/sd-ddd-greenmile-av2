package com.greenmile.dddeventswebflux.repositories

import com.greenmile.dddeventswebflux.domain.Stop
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface StopRepository: ReactiveMongoRepository<Stop, Int>