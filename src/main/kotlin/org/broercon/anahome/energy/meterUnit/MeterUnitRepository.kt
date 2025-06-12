package org.broercon.anahome.energy.meterUnit

import org.broercon.anahome.energy.meter.MeterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MeterUnitRepository : JpaRepository<MeterUnitEntity, Long> {

    @Query("SELECT c FROM MeterUnitEntity c WHERE c.meterEntity.id = :meterId")
    fun getAllByMeter(@Param("meterId") meterId: Long): List<MeterUnitEntity>
}