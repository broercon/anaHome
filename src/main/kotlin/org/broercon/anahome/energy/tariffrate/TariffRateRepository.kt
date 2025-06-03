package org.broercon.anahome.energy.tariffrate

import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TariffRateRepository : JpaRepository<TariffRateEntity, Long> {
    fun findByTariffPlanId(tariffPlanId: Long): List<TariffRateEntity>

    @Query("SELECT c " +
            "FROM TariffRateEntity c " +
            "WHERE c.tariffPlan.id = :tariffPlanId " +
            "AND c.effectiveFrom >= :from " +
            "AND c.effectiveFrom <= :end " +
            "AND (c.effectiveTo < :end or c.effectiveTo is null)" )
    fun getByTariffPlanAndPeriod(@Param("tariffPlanId") tariffPlanId: Long,
                                @Param("from") from: LocalDateTime,
                                @Param("end") end: LocalDateTime): List<TariffRateEntity>
}