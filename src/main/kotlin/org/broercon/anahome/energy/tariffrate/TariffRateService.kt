package org.broercon.anahome.energy.tariffrate

import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TariffRateService (private val repository: TariffRateRepository) {
    fun create(tariffRateEntity: TariffRateEntity): TariffRateEntity {
        validate(tariffRateEntity)
        return repository.save(tariffRateEntity)
    }

    fun save(id: Long?, tariffRateEntity: TariffRateEntity) : TariffRateEntity {
        // Check ID and Class structure should have the same id
        if (id != tariffRateEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        // If ID is take over -> Then the ID should exist in Database
        if (id == tariffRateEntity.id) getById(id = id)
        // Check only one of the foreign keys is take over
        validate(tariffRateEntity)
        return repository.save(tariffRateEntity)
    }

    fun getAll(): List<TariffRateEntity> = repository.findAll()

    fun getById(id: Long?) : TariffRateEntity = repository.findById(id?:0)
        .orElseThrow { EntityNotFoundException("TariffPlan with id $id not found") }

    fun getAllByTariffPlan(tariffPlanId: Long) = repository.findByTariffPlanId(tariffPlanId)

    fun getByTariffPlanAndPeriod(tariffPlanId: Long, start: LocalDateTime, end: LocalDateTime ) =
        repository.getByTariffPlanAndPeriod(tariffPlanId, start, end)

    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }


    private fun validate(entity: TariffRateEntity) {
        if (entity.effectiveTo != null && entity.effectiveTo.isBefore(entity.effectiveFrom)) {
            throw IllegalArgumentException("Effective to date must be after effective from date!")
        }

        if (entity.tariffPlan == null) throw IllegalArgumentException("TariffPlan must be set!")

        validateNoOverlap(entity)

        validatePlanDates(entity)
    }

    private fun validateNoOverlap(new: TariffRateEntity) {
        // Find existing plans for the same meter type or meter
        var existingPlans: List<TariffRateEntity> = repository.findByTariffPlanId(new.tariffPlan!!.id!!)
        existingPlans = existingPlans.filter { it.id != new.id }
        val hasOverlap = existingPlans.any { existingPlan ->
            datesOverlap(
                start1 = new.effectiveFrom,
                end1 = new.effectiveTo,
                start2 = existingPlan.effectiveFrom,
                end2 = existingPlan.effectiveTo
            )
        }
        if (hasOverlap) {
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

    private fun validatePlanDates(entity: TariffRateEntity) {
        val plan: TariffPlanEntity = entity.tariffPlan!!
        if (plan.effectiveTo != null && entity.effectiveFrom.isBefore(plan.effectiveFrom)) {
            throw IllegalArgumentException("Effective from date must be after effective from date of tariff plan!")
        }
        if (plan.effectiveFrom.isAfter(plan.effectiveTo)) {
            throw IllegalArgumentException("Effective from date must be before effective to date of tariff plan!")
        }
    }
}