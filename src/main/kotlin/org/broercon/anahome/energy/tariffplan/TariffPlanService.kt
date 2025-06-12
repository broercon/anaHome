package org.broercon.anahome.energy.tariffplan

import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TariffPlanService (private val repository: TariffPlanRepository) {
    fun create(tariffPlanEntity: TariffPlanEntity): TariffPlanEntity {
        validate(tariffPlanEntity)
        return repository.save(tariffPlanEntity)
    }

    fun save(id: Long?, tariffPlanEntity: TariffPlanEntity) : TariffPlanEntity {
        // Check ID and Class structure should have the same id
        if (id != tariffPlanEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        // If ID is take over -> Then the ID should exist in Database
        if (id == tariffPlanEntity.id) getById(id = id)
        // Check only one of the foreign keys is take over
        validate(tariffPlanEntity)
        return repository.save(tariffPlanEntity)
    }

    fun getAll(): List<TariffPlanEntity?> = repository.findAll()


    fun getById(id: Long?) : TariffPlanEntity = repository.findById(id)
        .orElseThrow { EntityNotFoundException("TariffPlan with id $id not found") }

    fun getByMeterType(id: Long) = repository.findAll().filter {
        it.meterType == null || it.meterType!!.id == id
    }

    fun getByMeterTypeAndPeriod(id: Long, start: LocalDateTime, end: LocalDateTime) = repository.getByMeterTypeAndPeriod(id, start, end)

    fun getByMeterAndPeriod(id: Long, start: LocalDateTime, end: LocalDateTime) = repository.getByMeterAndPeriod(id, start, end)

    fun getByMeterUnitAndPeriod(id: Long, start: LocalDateTime, end: LocalDateTime) = repository.getByMeterUnitAndPeriod(id, start, end)


    fun findActivePlans() : List<TariffPlanEntity> = repository.findAll().filter {
        it.effectiveTo == null || it.effectiveTo.isAfter(LocalDateTime.now())
    }

    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }


    private fun validate(entity: TariffPlanEntity) {
        val count = listOf(entity.meter, entity.meterUnit, entity.meterType).count { it != null }
        if (count != 1) throw IllegalArgumentException("Exactly one meter must be set!")

        if (entity.effectiveTo != null && entity.effectiveTo.isBefore(entity.effectiveFrom)) {
            throw IllegalArgumentException("Effective to date must be after effective from date!")
        }

        validateNoOverlapWithMeterType(entity)
    }

    private fun validateNoOverlapWithMeterType(newPlan: TariffPlanEntity) {
        // Find existing plans for the same meter type or meter
        var existingPlans: List<TariffPlanEntity?>? = if (newPlan.meterType != null) getByMeterType(newPlan.meterType!!.id) else null
        existingPlans = existingPlans?.filter { it?.id != newPlan.id }
        val hasOverlap = existingPlans?.any { existingPlan ->
            datesOverlap(
                start1 = newPlan.effectiveFrom,
                end1 = newPlan.effectiveTo,
                start2 = existingPlan?.effectiveFrom,
                end2 = existingPlan?.effectiveTo
            )
        }
        if (hasOverlap ?: false) {
            throw IllegalStateException("New tariff plan overlaps with existing plan")
        }
    }


    private fun datesOverlap(start1: LocalDateTime, end1: LocalDateTime?, start2: LocalDateTime?, end2: LocalDateTime?): Boolean {
        // Both existing Values are null -> No overlap
        if (start2 == null && end2 == null) return false

        // Both plans must have their dates in correct order
        // start2 cannot null, because start is only null, when end2 is also null -> If bevor
        if ((end1 != null && start1 >= end1) || (end2 != null && start2!! >= end2)) {
            throw IllegalArgumentException("Start date must be before end date")
        }

        return when {
            // If both have end dates
            end1 != null && end2 != null -> {
                start1 <= end2 && end1 >= start2
            }
            // If only the first plan has no end date
            end1 == null && end2 != null -> {
                start1 <= end2
            }
            // If only the second plan has no end date
            end1 != null && end2 == null -> {
                start2!! <= end1
            }
            // If both have no end date
            else -> true // Always overlap if both have no end date
        }
    }

}