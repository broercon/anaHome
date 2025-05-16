package org.broercon.anahome.energy.model


import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.broercon.anahome.energy.meter.MeterEntity

@Entity
@Table(name = "meter_units")
data class MeterUnit(
    @Id
    val id: Long,

    val label: String, // e.g. "HT", "NT"

    val unit: String,  // e.g. "kWh", "m³"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id")
    val meterEntity: MeterEntity
)