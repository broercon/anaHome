package org.broercon.anahome.energy.tariffplan

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import java.time.LocalDateTime

@Entity
@Table(name = "tariff_plans")
data class TariffPlanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val name: String,

    val effectiveFrom: LocalDateTime,
    val effectiveTo: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "meter_type_id")
    var meterType: MeterTypeEntity?,  // Optional, if you want to associate with type

    @ManyToOne
    @JoinColumn(name = "meter_id")
    var meter: MeterEntity?,  // Optional, if you want to associate with specific meter

    @ManyToOne
    @JoinColumn(name = "meter_unit_id")
    var meterUnit: MeterUnitEntity?,  // Link to specific unit (e.g., HT or NT for electricity)
    )
