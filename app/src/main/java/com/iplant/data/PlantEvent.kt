package com.iplant.data

import java.time.LocalDate

interface PlantEvent {
    fun getEventDate(): LocalDate
    fun getIcon(): Int
}