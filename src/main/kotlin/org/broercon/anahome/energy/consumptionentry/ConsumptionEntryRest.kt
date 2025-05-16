package org.broercon.anahome.energy.consumptionentry


import org.broercon.anahome.energy.meterUnit.MeterUnitService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

data class ConsumptionEntryRest(
    val id: Long,
    val timestamp: LocalDateTime,
    val meterReading: Double,
    val comment: String? = null,
    val meterUnitEntityId: Long
)

@Component
class ConsumptionEntryMapper(private val meterUnitService: MeterUnitService) {
    // Extension function (Receiver function)
    fun ConsumptionEntryRest.toDomain(): ConsumptionEntryEntity = ConsumptionEntryEntity(
        id = this.id,
        timestamp = this.timestamp,
        meterReading = this.meterReading,
        comment = this.comment,
        meterUnitEntity = meterUnitService.getById(this.meterUnitEntityId),
    )

    fun ConsumptionEntryEntity?.toRest(): ConsumptionEntryRest = ConsumptionEntryRest(
        id = this?.id ?: 0,
        timestamp = this?.timestamp ?: LocalDateTime.now(),
        meterReading = this?.meterReading ?: 0.0,
        comment = this?.comment,
        meterUnitEntityId = this?.meterUnitEntity?.id ?: 0,
    )

    fun List<ConsumptionEntryEntity?>.toRest(): List<ConsumptionEntryRest?> = this.map { it?.toRest() }
}
