package org.broercon.anahome.energy.meterUnit


import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.broercon.anahome.energy.meter.MeterEntity

@Entity
@Table(name = "meter_units")
data class MeterUnitEntity(
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    var id: Long? = null,

    val label: String, // e.g. "HT", "NT"

    val unit: String,  // e.g. "kWh", "m³"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id")
    val meterEntity: MeterEntity?
)