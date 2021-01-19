package com.greenmile.dddeventswebflux.repositories

import com.greenmile.dddeventswebflux.domain.Telephone
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface TelephoneRepository: ReactiveMongoRepository<Telephone,Int>