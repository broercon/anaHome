package org.anaHome.org.broercon.anahome.energy.model


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID
import java.time.LocalDateTime

@Entity
@Table(name = "consumption_entries")
data class ConsumptionEntry(
    @Id
    val id: Long,

    val timestamp: LocalDateTime,

    @Column(name = "meter_reading")
    val meterReading: Double,

    val comment: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_unit_id")
    val meterUnit: MeterUnit
)