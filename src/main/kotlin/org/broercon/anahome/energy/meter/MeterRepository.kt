package org.broercon.anahome.energy.meter

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface MeterRepository : JpaRepository<MeterEntity, Long> {
    fun findByName(name: String): MeterEntity?

    fun findByMeterTypeEntityId(id: Long): List<MeterEntity>
}