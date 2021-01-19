package com.greenmile.dddeventswebflux.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "employee")
data class Employee(
        @Id
        val id: Int,
        val telephone: Telephone,
        val name: String,
        val cpf: String,
) {
}