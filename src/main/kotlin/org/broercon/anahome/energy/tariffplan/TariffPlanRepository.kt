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
            "AND (c.effectiveTo is null or " +
            "     c.effectiveTo <= :end )" )
    fun getByMeterUnitAndPeriod(@Param("meterUnitId") meterUnitId: Long,
                                @Param("from") from: LocalDateTime,
                                @Param("end") end: LocalDateTime): List<TariffPlanEntity>


    @Query("SELECT c " +
            "FROM TariffPlanEntity c " +
            "WHERE c.meter.id = :meterId " +
            "AND c.effectiveFrom >= :from " +
            "AND (c.effectiveTo is null or " +
            "     c.effectiveTo <= :end )" )
    fun getByMeterAndPeriod(@Param("meterId") meterId: Long,
                                @Param("from") from: LocalDateTime,
                                @Param("end") end: LocalDateTime): List<TariffPlanEntity>

    @Query("SELECT c " +
            "FROM TariffPlanEntity c " +
            "WHERE c.meterType.id = :meterTypeId " +
            "AND c.effectiveFrom <= :from " +
            "AND (c.effectiveTo is null or " +
            "     c.effectiveTo <= :end )" )
    fun getByMeterTypeAndPeriod(@Param("meterTypeId") meterTypeId: Long,
                                @Param("from") from: LocalDateTime,
                                @Param("end") end: LocalDateTime): List<TariffPlanEntity>
}
