package org.broercon.anahome.energy.meter


import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MeterService (private val repository: MeterRepository) {

    private var logger = KotlinLogging.logger {}


    fun getAll(): List<MeterEntity> = repository.findAll()
    fun getByName(name: String): MeterEntity? = repository.findByName(name)
    fun create(MeterEntity: MeterEntity): MeterEntity = repository.save(MeterEntity)
    fun save(id: Long, MeterEntity: MeterEntity) : MeterEntity {
        if (id != MeterEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        getById(MeterEntity.id)
        return repository.save(MeterEntity)
    }
    fun getById(id :Long) : MeterEntity? {
        val meter: MeterEntity? = repository.findByIdOrNull(id)
        if (meter == null) throw EntityNotFoundException("Meter not found with id: $id")
        return meter
    }

    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }
}