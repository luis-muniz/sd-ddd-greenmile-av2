package com.greenmile.dddeventswebflux.events

import com.greenmile.dddeventswebflux.DTOs.NotificationDTO
import com.greenmile.dddeventswebflux.domain.Event
import com.greenmile.dddeventswebflux.domain.Route
import com.greenmile.dddeventswebflux.domain.Stop
import com.greenmile.dddeventswebflux.domain.enums.EventType
import com.greenmile.dddeventswebflux.repositories.EventRepository
import com.greenmile.dddeventswebflux.repositories.RouteRepository
import com.greenmile.dddeventswebflux.repositories.StopRepository
import com.greenmile.dddeventswebflux.utils.haversineDistance
import com.luis.avaliacao.domain.Coordinate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.util.*

const val geoFence = 0.03 // 30 meters

@Component
class EventArrival(
    private val routeRepository: RouteRepository,
    private val eventRepository: EventRepository
){

    private val log = LoggerFactory.getLogger(EventArrival::class.java)

    fun processCoordinate(notificationDTO: NotificationDTO){

        if (notificationDTO.actualCoordinateSensorGPSFromTelephone == null || notificationDTO.actualCoordinateSensorGPSFromVehicle == null || notificationDTO.lastCoordinateSensorGPSFromTelephone == null || notificationDTO.lastCoordinateSensorGPSFromVehicle == null){
            throw Error("Coordenadas invalidas")
        }

        val routeId = notificationDTO.routeId
        val lastCoordinateVehicle = notificationDTO.lastCoordinateSensorGPSFromVehicle
        val actualCoordinateVehicle = notificationDTO.actualCoordinateSensorGPSFromVehicle

        if (lastCoordinateVehicle.longitude == actualCoordinateVehicle.longitude && lastCoordinateVehicle.latitude == actualCoordinateVehicle.latitude){
            this.routeRepository.findById(routeId)
                .map{ route ->
                    Pair(filterListStops(actualCoordinateVehicle,route.stops).toFlux(),route)
                }
                .flatMap { pair ->
                    pair.first.flatMap { stop ->
                        arrivedStopOnRoute(pair.second, stop)
                            .flatMap {  registerEvent(pair.second,EventType.ARRIVE, stop)}
                    }.then()
                }.subscribe()
        }
    }

    private fun filterListStops(actualCoordinateVehicle: Coordinate,stops: List<Stop>) = stops.filter { stop ->
            stop.arrivalAt == null && haversineDistance(actualCoordinateVehicle.latitude,actualCoordinateVehicle.longitude,stop.coordinate.latitude,stop.coordinate.longitude) <= geoFence
    }

    private fun arrivedStopOnRoute(route: Route, oldStop:Stop): Mono<Route> {
        val updateStop = oldStop.copy(arrivalAt = Date())
        val indexOf = route.stops.indexOf(oldStop)
        route.stops.removeAt(indexOf)
        route.stops.add(updateStop)
        return routeRepository.save(route)
    }

    private fun registerEvent(route: Route, eventType: EventType, stop:Stop): Mono<Event>{
        this.log.info("++++++++++++++++++++++++++++++++++++++++")
        this.log.info("-----Event Arrival--------")
        this.log.info("{} arrive at stop in client: {}", route.vehicle.plate, stop.address)
        this.log.info("++++++++++++++++++++++++++++++++++++++++")
        val newEvent = Event(eventType = EventType.ARRIVE, `when` = Date())
        return this.eventRepository.save(newEvent)
    }
}