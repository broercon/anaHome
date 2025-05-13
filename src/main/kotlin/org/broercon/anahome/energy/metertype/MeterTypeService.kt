package org.anaHome.org.broercon.anahome.energy.metertype

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MeterTypeService (private val repository: MeterTypeRepository) {
    fun getAll(): List<MeterTypeEntity> = repository.findAll()
    fun getByName(name: String): MeterTypeEntity? = repository.findByName(name)
    fun create(meterTypeEntity: MeterTypeEntity): MeterTypeEntity = repository.save(meterTypeEntity)
    fun save(id: Long, meterTypeEntity: MeterTypeEntity) : MeterTypeEntity {
        if (id != meterTypeEntity.id && id != 0.toLong()) throw IllegalArgumentException("ID does not match the transferred data record")
        getById(meterTypeEntity.id)
        return repository.save(meterTypeEntity)
    }
    fun getById(id :Long) : MeterTypeEntity? = repository.findByIdOrNull(id)
        ?: throw EntityNotFoundException("MeterType with id $id not found")
}