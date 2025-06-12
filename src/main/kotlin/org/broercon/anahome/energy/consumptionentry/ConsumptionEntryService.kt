package org.broercon.anahome.energy.consumptionentry

import jakarta.persistence.EntityNotFoundException
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ConsumptionEntryService (private val repository: ConsumptionEntryRepository) {

    private var logger = KotlinLogging.logger {}

    fun getAll(): List<ConsumptionEntryEntity> = repository.findAll()

    fun getById(id :Long) : ConsumptionEntryEntity? {
        val meter: ConsumptionEntryEntity? = repository.findByIdOrNull(id)
        if (meter == null) throw EntityNotFoundException("Meter not found with id: $id")
        return meter
    }

    fun getVolumesByUnit(id: Long): List<ConsumptionVolumesRest>{
        val byUnit = repository.getByUnit(id).sortedBy { it.timestamp }
        return byUnit.zipWithNext().map({ (prev, next) ->
            ConsumptionVolumesRest(
                next.meterReading - prev.meterReading,
                start = prev.timestamp,
                end = next.timestamp
            )
        })
    }

    fun create(ConsumptionEntryEntity: ConsumptionEntryEntity): ConsumptionEntryEntity = repository.save(ConsumptionEntryEntity)
    fun save(id: Long, ConsumptionEntryEntity: ConsumptionEntryEntity) : ConsumptionEntryEntity {
        if (id != ConsumptionEntryEntity.id && id != 0.toLong()) throw EntityNotFoundException("ID does not match the transferred data record")
        getById(ConsumptionEntryEntity.id)
        return repository.save(ConsumptionEntryEntity)
    }


    fun delete(id: Long) {
        getById(id)
        repository.deleteById(id)
    }

    fun getVolumeByMeterTypeAndPeriod(idMeterType: Long, start: LocalDateTime, end: LocalDateTime): Double {
       return repository.getVolumeByMeterTypeAndPeriod(idMeterType, start, end) ?: 0.toDouble()
    }

    fun isVolumeByMeterTypeAndPeriod(idMeterType: Long, start: LocalDateTime, end: LocalDateTime): Boolean {
        return repository.isVolumeByMeterTypeAndPeriod(idMeterType, start, end) ?: false
    }
}

