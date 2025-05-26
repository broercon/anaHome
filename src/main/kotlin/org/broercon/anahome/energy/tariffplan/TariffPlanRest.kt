package org.broercon.anahome.energy.tariffplan

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.meterUnit.MeterUnitService
import org.broercon.anahome.energy.metertype.MeterTypeService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

data class TariffPlanRest(
    @field:NotNull
    val id: Long? = null,

    @field:NotNull
    @field:NotEmpty
    val name: String,

    @field:NotNull
    val effectiveFrom: LocalDateTime,

    val effectiveTo: LocalDateTime? = null,

    // Ref IDs
    val meterTypeId: Long?,  // Optional, if you want to associate with type
    val meterId: Long?,  // Optional, if you want to associate with specific meter
    val meterUnitId: Long?,  // Link to specific unit (e.g., HT or NT for electricity)
    )

@Component
class TariffPlanMapper(private val meterService: MeterService,
                      private val meterTypeService: MeterTypeService,
                      private val meterUnitService: MeterUnitService) {
    // Extension function (Receiver function)
    fun TariffPlanRest.toDomain(): TariffPlanEntity {
        var tariffPlanEntity = TariffPlanEntity(
            id = this.id,
            name = this.name,
            effectiveFrom = this.effectiveFrom,
            effectiveTo = this.effectiveTo,
            meterType = null,
            meter = null,
            meterUnit = null
        )

        if (this.meterTypeId != null ) tariffPlanEntity.meterType = meterTypeService.getById(this.meterTypeId)
        if (this.meterId != null) tariffPlanEntity.meter = meterService.getById(this.meterId)
        if (this.meterUnitId != null) tariffPlanEntity.meterUnit = meterUnitService.getById(this.meterUnitId)
        return tariffPlanEntity
    }

    fun TariffPlanEntity?.toRest(): TariffPlanRest = TariffPlanRest(
        id = this?.id ?: 0,
        name = this?.name ?: "default",
        effectiveFrom = this?.effectiveFrom ?: LocalDateTime.now(),
        effectiveTo = this?.effectiveTo ?: LocalDateTime.now(),
        meterTypeId = this?.meterType?.id ?: 0,
        meterId = this?.meter?.id ?: 0,
        meterUnitId = this?.meterUnit?.id ?: 0,
    )

    fun List<TariffPlanEntity?>.toRest(): List<TariffPlanRest?> = this.map { it.toRest() }
}