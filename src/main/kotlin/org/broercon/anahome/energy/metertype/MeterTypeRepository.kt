package org.broercon.anahome.energy.metertype

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


class EnergyRepository


@Repository
interface MeterTypeRepository : JpaRepository<MeterTypeEntity, Long> {
    fun findByName(name: String): MeterTypeEntity?
}