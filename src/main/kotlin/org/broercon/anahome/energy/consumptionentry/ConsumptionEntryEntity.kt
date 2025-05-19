package org.broercon.anahome.energy.consumptionentry


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import java.time.LocalDateTime

@Entity
@Table(name = "consumption_entries")
data class ConsumptionEntryEntity(
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    val id: Long,

    val timestamp: LocalDateTime,

    @Column(name = "meter_reading")
    val meterReading: Double,

    val comment: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_unit_id")
    val meterUnitEntity: MeterUnitEntity
)