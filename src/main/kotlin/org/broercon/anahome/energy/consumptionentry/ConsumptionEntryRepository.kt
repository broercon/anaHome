package org.broercon.anahome.energy.consumptionentry

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface ConsumptionEntryRepository : JpaRepository<ConsumptionEntryEntity, Long> {
    @Query("SELECT c FROM ConsumptionEntryEntity c WHERE c.meterUnitEntity.id = :unitId")
    fun getByUnit(@Param("unitId") unitId: Long): List<ConsumptionEntryEntity>
}