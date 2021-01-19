package com.greenmile.dddeventswebflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DddEventsWebfluxApplication

fun main(args: Array<String>) {
	runApplication<DddEventsWebfluxApplication>(*args)
}
