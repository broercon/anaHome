package org.broercon.anahome.energy.tariffplan

import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TariffPlanRepository : JpaRepository<TariffPlanEntity, Long> {
    @Query("SELECT c " +
            "FROM TariffPlanEntity c " +
            "WHERE c.meterUnit.id = :meterUnitId " +
            "AND c.effectiveFrom >= :from " +
            "AND c.effectiveFrom <= :end " +
            "AND (c.effectiveTo < :end or c.effectiveTo is null)" )
    fun getByMeterUnitAndPeriod(@Param("meterUnitId") meterUnitId: Long,
                                @Param("from") from: LocalDateTime,
                                @Param("end") end: LocalDateTime): List<MeterUnitEntity>
}
