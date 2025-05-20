package org.broercon.anahome.energy.consumptionentry

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ConsumptionEntryRepository : JpaRepository<ConsumptionEntryEntity, Long>