package org.broercon.anahome.energy.metertype

import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.meter.MeterEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MeterTypeService (private val repository: MeterTypeRepository) {
    fun getAll(): List<MeterTypeEntity> = repository.findAll()
    fun getByName(name: String): MeterTypeEntity? = repository.findByName(name)
    fun create(meterTypeEntity: MeterTypeEntity): MeterTypeEntity = repository.save(meterTypeEntity)
    fun save(id: Long, meterTypeEntity: MeterTypeEntity) : MeterTypeEntity {
        if (id != meterTypeEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        getById(meterTypeEntity.id)
        return repository.save(meterTypeEntity)
    }
    fun getById(id: Long?) : MeterTypeEntity {
        val meterTypeEntity: MeterTypeEntity? = repository.findByIdOrNull(id)
        if (meterTypeEntity == null) throw EntityNotFoundException("MeterTypeEntity not found with id: $id")
        return meterTypeEntity
    }
}