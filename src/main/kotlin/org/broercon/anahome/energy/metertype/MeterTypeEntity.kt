package org.anaHome.org.broercon.anahome.energy.metertype

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "meter_types")
data class MeterTypeEntity(
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    val id: Long,

    // Zum Beispiel Strom/Gas/Wasser
    val name: String
)