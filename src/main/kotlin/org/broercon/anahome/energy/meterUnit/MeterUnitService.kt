package org.broercon.anahome.energy.meterUnit

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class MeterUnitService (private val repository: MeterUnitRepository) {
    fun getAll(): List<MeterUnitEntity?> = repository.findAll()

    fun getById(id: Long?) : MeterUnitEntity = repository.findById(id)
        .orElseThrow { EntityNotFoundException("MeterUnit with id $id not found") }

    fun getAllByMeter(id: Long) : List<MeterUnitEntity> = repository.getAllByMeter(id)

    fun create(meterUnitEntity: MeterUnitEntity): MeterUnitEntity = repository.save(meterUnitEntity)

    fun save(id: Long, meterUnitEntity: MeterUnitEntity) : MeterUnitEntity {
        if (id != meterUnitEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        getById(meterUnitEntity.id)
        return repository.save(meterUnitEntity)
    }

    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }
}