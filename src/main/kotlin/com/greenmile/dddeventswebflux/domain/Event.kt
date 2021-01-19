package com.greenmile.dddeventswebflux.domain
import com.greenmile.dddeventswebflux.domain.enums.EventType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "event")
data class Event (
        @Id
        val _id: String? = null,
        val eventType: EventType,
        val `when`: Date = Date(),
        ){
}