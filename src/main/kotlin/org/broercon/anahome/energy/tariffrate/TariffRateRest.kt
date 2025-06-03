package org.broercon.anahome.energy.tariffrate

import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Pattern
import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.meterUnit.MeterUnitService
import org.broercon.anahome.energy.metertype.MeterTypeService
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import org.broercon.anahome.energy.tariffplan.TariffPlanRest
import org.broercon.anahome.energy.tariffplan.TariffPlanService
import org.jetbrains.annotations.NotNull
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

data class TariffRateRest(
    @field:NotNull
    val id: Long? = null,

    @field:Digits(integer = 3, fraction = 2, message = "Maximum 3 integer digits and 2 decimal places")
    val unitPrice: Double,

    @field:NotNull
    @field:Pattern(regexp = "GP|AP", message = "Value must be either 'GP' or 'AP'")
    val unit: String,

    @field:NotNull
    val effectiveFrom: LocalDateTime,

    val effectiveTo: LocalDateTime? = null,

    @field:NotNull
    val tariffPlanId: Long,

    )


@Component
class TariffRateMapper(private val tariffPlanService: TariffPlanService) {
    // Extension function (Receiver function)
    fun TariffRateRest.toDomain(): TariffRateEntity {
        var tariffRateEntity = TariffRateEntity(
            id = this.id,
            effectiveFrom = this.effectiveFrom,
            effectiveTo = this.effectiveTo,
            tariffPlan = null,
            unit = this.unit,
            unitPrice = this.unitPrice
        )

        tariffRateEntity.tariffPlan = tariffPlanService.getById(this.tariffPlanId)

        return tariffRateEntity
    }

    fun TariffRateEntity.toRest(): TariffRateRest = TariffRateRest(
        id = this.id,
        effectiveFrom = this.effectiveFrom,
        effectiveTo = this.effectiveTo,
        unitPrice = this.unitPrice,
        unit = this.unit,
        tariffPlanId = this.tariffPlan!!.id!!,
    )

    fun List<TariffRateEntity>.toRest(): List<TariffRateRest> = this.map { it.toRest() }
}