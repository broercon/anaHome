package org.broercon.anahome.energy.consumptionentry

import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ConsumptionEntryService (private val repository: ConsumptionEntryRepository) {

    private var logger = KotlinLogging.logger {}

    fun getAll(): List<ConsumptionEntryEntity> = repository.findAll()

    fun create(ConsumptionEntryEntity: ConsumptionEntryEntity): ConsumptionEntryEntity = repository.save(ConsumptionEntryEntity)
    fun save(id: Long, ConsumptionEntryEntity: ConsumptionEntryEntity) : ConsumptionEntryEntity {
        if (id != ConsumptionEntryEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        getById(ConsumptionEntryEntity.id)
        return repository.save(ConsumptionEntryEntity)
    }
    fun getById(id :Long) : ConsumptionEntryEntity? {
        val meter: ConsumptionEntryEntity? = repository.findByIdOrNull(id)
        if (meter == null) throw EntityNotFoundException("Meter not found with id: $id")
        return meter
    }

    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }
}