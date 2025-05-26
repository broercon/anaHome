package org.broercon.anahome.energy.meterUnit

import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.tariffplan.TariffPlanRest
import org.springframework.stereotype.Component

data class MeterUnitRest (
    val id: Long,
    val label: String, // e.g. "HT", "NT"
    val unit: String,  // e.g. "kWh", "m³"
    val meterEntityId: Long
)

@Component
class MeterUnitMapper(private val meterService: MeterService) {
    // Extension function (Receiver function)
    fun MeterUnitRest.toDomain(): MeterUnitEntity = MeterUnitEntity(
        id = this.id,
        label = this.label,
        unit = this.unit,
        meterEntity = meterService.getById(this.meterEntityId)
    )

    fun MeterUnitEntity?.toRest(): MeterUnitRest = MeterUnitRest(
        id = this?.id ?: 0,
        label = this?.label ?: "default",
        unit = this?.unit ?: "default",
        meterEntityId = this?.meterEntity?.id ?: 0
    )

    fun List<MeterUnitEntity?>.toRest(): List<MeterUnitRest?> = this.map { it?.toRest() }
}
