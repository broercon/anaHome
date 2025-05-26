package org.broercon.anahome.energy.tariffplan

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TariffPlanRepository : JpaRepository<TariffPlanEntity, Long>