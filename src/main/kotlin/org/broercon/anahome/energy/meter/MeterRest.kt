package org.broercon.anahome.energy.meter

import org.broercon.anahome.energy.metertype.MeterTypeService
import org.springframework.stereotype.Component

data class MeterRest(
    val id: Long,

    val name: String,

    val meterNumber: String,

    val location: String,

    val meterTypeEntityId: Long
)

@Component
class MeterMapper(private val meterTypeService: MeterTypeService) {
    // Extension function (Receiver function)
    fun MeterRest.toDomain(): MeterEntity = MeterEntity(
        id = this.id,
        name = this.name,
        meterNumber = this.meterNumber,
        location = this.location,
        meterTypeEntity = meterTypeService.getById(this.meterTypeEntityId)
    )

    fun MeterEntity?.toRest(): MeterRest = MeterRest(
        id = this?.id ?: 0,
        name = this?.name ?: "default",
        meterNumber = this?.meterNumber ?: "default",
        location = this?.location ?: "default",
        meterTypeEntityId = this?.meterTypeEntity?.id ?: 0
    )

    fun List<MeterEntity?>.toRest(): List<MeterRest?> = this.map { it?.toRest() }
}
