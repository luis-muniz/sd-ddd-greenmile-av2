package com.greenmile.dddeventswebflux.repositories

import com.greenmile.dddeventswebflux.domain.Event
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface EventRepository: ReactiveMongoRepository<Event, String>