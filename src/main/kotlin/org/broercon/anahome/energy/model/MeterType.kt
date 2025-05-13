package org.anaHome.org.broercon.anahome.energy.model


import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity(name = "meter_types")
data class MeterType(
    @Id
    val id: String = UUID.randomUUID().toString(),

    // Zum Beispiel Strom/Gas/Wasser
    val name: String
)