package org.broercon.anahome.energy.consumptionentry

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface ConsumptionEntryRepository : JpaRepository<ConsumptionEntryEntity, Long> {
    @Query("SELECT c FROM ConsumptionEntryEntity c WHERE c.meterUnitEntity.id = :unitId")
    fun getByUnit(@Param("unitId") unitId: Long): List<ConsumptionEntryEntity>


    @Query("""
        WITH sum_Unit as (
            SELECT mr.meter_unit_id , MAX(meter_reading) - MIN(meter_reading) AS volume
                FROM public.consumption_entries mr
                JOIN public.meter_units mu ON mr.meter_unit_id = mu.id
                JOIN public.meter m ON mu.meter_id = m.id
                JOIN public.meter_types mh ON m.meter_type_id = mh.id
              WHERE mh.id = :meterTypeId
                  AND mr.timestamp BETWEEN :from AND :to
                  GROUP BY 
                    mr.meter_unit_id )
                    SELECT SUM(volume) AS total_volume
            FROM sum_Unit
        """,nativeQuery = true)
    fun getVolumeByMeterTypeAndPeriod(
        @Param("meterTypeId") meterTypeId: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): Double?

    @Query("""
        SELECT count(distinct mr.timestamp) >= 2
            FROM consumption_entries mr
            JOIN meter_units mu ON mr.meter_unit_id = mu.id
            JOIN meter m ON mu.meter_id = m.id
            JOIN meter_types mh ON m.meter_type_id = mh.id
           WHERE mh.id = :meterTypeId
             AND ( mr.timestamp = :from 
              OR mr.timestamp = :to )
        """,nativeQuery = true)
    fun isVolumeByMeterTypeAndPeriod(
        @Param("meterTypeId") meterTypeId: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): Boolean?
}