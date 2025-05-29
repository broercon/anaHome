package org.broercon.anahome.energy.tariffrate

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TariffRateRepository : JpaRepository<TariffRateEntity, Long> {
    fun findByTariffPlanId(tariffPlanId: Long): List<TariffRateEntity>
}