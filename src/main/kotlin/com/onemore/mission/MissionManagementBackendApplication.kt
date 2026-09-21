package com.onemore.mission

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MissionManagementBackendApplication

fun main(args: Array<String>) {
	runApplication<MissionManagementBackendApplication>(*args)
}
