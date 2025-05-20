package org.broercon.anahome.energy.meterUnit

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MeterUnitRepository : JpaRepository<MeterUnitEntity, Long>