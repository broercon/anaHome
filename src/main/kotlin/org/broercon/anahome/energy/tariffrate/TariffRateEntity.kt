package org.broercon.anahome.energy.tariffrate

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "tariff_rates")
data class TariffRateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "tariff_plan_id")
    var tariffPlan: TariffPlanEntity? = null,

    val unitPrice: Double,

    val unit: String,

    val effectiveFrom: LocalDateTime,
    val effectiveTo: LocalDateTime? = null

    )
