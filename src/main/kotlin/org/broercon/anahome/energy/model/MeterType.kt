package org.anaHome.org.broercon.anahome.energy.model


import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity(name = "meter_types")
data class MeterType(
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    val id: Long,

    // Zum Beispiel Strom/Gas/Wasser
    val name: String
)