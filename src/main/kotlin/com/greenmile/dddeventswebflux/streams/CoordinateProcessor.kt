package com.greenmile.dddeventswebflux.streams

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.greenmile.dddeventswebflux.domain.SensorGPS
import com.greenmile.dddeventswebflux.DTOs.NotificationDTO
import com.greenmile.dddeventswebflux.DTOs.SensorsRoutesDTO
import com.greenmile.dddeventswebflux.events.EventArrival
import com.greenmile.dddeventswebflux.events.EventEndangeredEmployee
import com.greenmile.dddeventswebflux.repositories.RouteRepository
//import com.greenmile.dddeventswebflux.events.EventEndangeredEmployee
import com.greenmile.dddeventswebflux.repositories.SensorGPSRepository
import com.luis.avaliacao.domain.Coordinate
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.Error
import java.time.Duration
import java.util.*

@Component
class CoordinateProcessor(
    private val routeRepository: RouteRepository,
    private val sensorGPSRepository: SensorGPSRepository,
    private val eventArrival: EventArrival,
    private val eventEndangeredEmployee: EventEndangeredEmployee
){

    private val log = LoggerFactory.getLogger(CoordinateProcessor::class.java)

    fun receiveCoordinates(sensorGPSFromVehicle: SensorGPS, sensorGPSFromTelephone: SensorGPS) : Mono<NotificationDTO>{
        this.log.info("---------COORDINATES RECEIVER--------")
        this.log.info("vehicle [{}]",sensorGPSFromVehicle)
        this.log.info("telephone [{}]",sensorGPSFromTelephone)
        this.log.info("")

        if (sensorGPSFromVehicle.coordinate == null || sensorGPSFromTelephone.coordinate == null){
            throw Error("Failed sensors coordinates")
        }

        return this.routeRepository.getRouteBySensorGPSIdFromVehicle(sensorGPSFromVehicle.id)
            .flatMap { route ->
                val lastCoordinateSensorGPSVehicle = if (route.vehicle.sensorGPS.coordinate != null){
                    val lastCoordinate = route.vehicle.sensorGPS.coordinate
                    route.vehicle.sensorGPS.coordinate = sensorGPSFromVehicle.coordinate

                    this.routeRepository.save(route).subscribe()
                    lastCoordinate
                }else{
                    val lastCoordinate = sensorGPSFromVehicle.coordinate
                    route.vehicle.sensorGPS.coordinate = Coordinate(id = sensorGPSFromVehicle.coordinate!!.id, latitude = sensorGPSFromVehicle.coordinate!!.latitude, longitude = sensorGPSFromVehicle.coordinate!!.longitude, datePing = sensorGPSFromVehicle.coordinate!!.datePing)
                    this.routeRepository.save(route).subscribe()
                    lastCoordinate
                }

                val lastCoordinateSensorGPSTelephone = if (route.employee.telephone.sensorGPS.coordinate != null){
                    val lastCoordinate = route.employee.telephone.sensorGPS.coordinate
                    route.employee.telephone.sensorGPS.coordinate = sensorGPSFromTelephone.coordinate
                    this.routeRepository.save(route).subscribe()
                    lastCoordinate
                }else{
                    val lastCoordinate = sensorGPSFromTelephone.coordinate
                    route.employee.telephone.sensorGPS.coordinate = sensorGPSFromTelephone.coordinate
                    this.routeRepository.save(route).subscribe()
                    lastCoordinate
                }
                Mono.just(NotificationDTO(route.id, lastCoordinateSensorGPSVehicle,sensorGPSFromVehicle.coordinate,lastCoordinateSensorGPSTelephone, sensorGPSFromTelephone.coordinate))
            }
            .switchIfEmpty(Mono.error(ClassNotFoundException("Sensor não corresponde a nenhuma rota")))
    }

    @Scheduled(fixedDelay = 1000000, initialDelay = 10000)
    fun consumeCoordinates(){
        val mapper = ObjectMapper().registerModule(KotlinModule())

        val jsonContentSensorsGPSRoute = "[{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110911,\"longitude\":-44.324189,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110911,\"longitude\":-44.324189,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110843,\"longitude\":-44.324396,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110843,\"longitude\":-44.324396,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110875,\"longitude\":-44.324546,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110875,\"longitude\":-44.324546,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110875,\"longitude\":-44.324546,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110875,\"longitude\":-44.324546,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110821,\"longitude\":-44.324771,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110821,\"longitude\":-44.324771,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110832,\"longitude\":-44.325554,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110832,\"longitude\":-44.325554,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110517,\"longitude\":-44.330221,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110517,\"longitude\":-44.330221,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110517,\"longitude\":-44.330221,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110517,\"longitude\":-44.330221,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110517,\"longitude\":-44.330221,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110517,\"longitude\":-44.330221,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110094,\"longitude\":-44.327163,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110094,\"longitude\":-44.327163,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110094,\"longitude\":-44.327163,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110094,\"longitude\":-44.327163,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.110094,\"longitude\":-44.327163,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.110094,\"longitude\":-44.327163,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}},{\"coordinateSensorGPSVehicle\":{\"id\":1,\"coordinate\":{\"id\":1,\"latitude\":-3.109405,\"longitude\":-44.329807,\"datePing\":1608493803}},\"coordinateSensorGPSTelephone\":{\"id\":2,\"coordinate\":{\"id\":2,\"latitude\":-3.108506,\"longitude\":-44.330329,\"datePing\":1608493803}}}]"

        Flux.fromIterable( mapper.readValue(jsonContentSensorsGPSRoute, Array<SensorsRoutesDTO>::class.java).asList())
            .delayElements(Duration.ofMillis(500))
            .flatMap { sensorsRoute ->
                    val updateDatePingCoordinateVehicle = sensorsRoute.coordinateSensorGPSVehicle.coordinate?.copy(datePing = Date())
                    val updateDatePingCoordinateTelephone = sensorsRoute.coordinateSensorGPSTelephone.coordinate?.copy(datePing = Date())

                    val updateSensorGPSVehicle =  sensorsRoute.coordinateSensorGPSVehicle.copy(coordinate = updateDatePingCoordinateVehicle)
                    val updateSensorGPSTelephone =  sensorsRoute.coordinateSensorGPSTelephone.copy(coordinate = updateDatePingCoordinateTelephone)

                    receiveCoordinates(updateSensorGPSVehicle,updateSensorGPSTelephone)
            }.map { notificationDTO ->
                eventArrival.processCoordinate(notificationDTO)
                eventEndangeredEmployee.processCoordinate(notificationDTO)
            }.subscribe()
    }
}