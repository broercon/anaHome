package org.broercon.anahome.energy.consumptionentry

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ConsumptionEntryServiceTest {

    @Mock
    private lateinit var repository: ConsumptionEntryRepository

    @InjectMocks
    private lateinit var service: ConsumptionEntryService

    private lateinit var meterEntity: MeterEntity
    private lateinit var meterUnitEntity: MeterUnitEntity
    private lateinit var consumptionEntryEntity: ConsumptionEntryEntity

    @BeforeEach
    fun setUp() {

        meterEntity = MeterEntity(
            id = 1,
            name = "Test Meter",
            meterNumber = "12345",
            location = "Test Location",
            meterTypeEntity = MeterTypeEntity(1, "Test Type")
        )

        meterUnitEntity = MeterUnitEntity(
            id = 1,
            label = "HT",
            unit = "kWh",
            meterEntity = meterEntity
        )

        consumptionEntryEntity = ConsumptionEntryEntity(
            id = 1L,
            timestamp = LocalDateTime.now(),
            meterReading = 123.45,
            comment = "Test reading",
            meterUnitEntity = meterUnitEntity
        )
    }

    @Test
    fun `getAll should return all entities`() {
        val expectedList = listOf(consumptionEntryEntity)
        `when`(repository.findAll()).thenReturn(expectedList)

        val result = service.getAll()

        assertEquals(expectedList, result)
        verify(repository).findAll()
    }

    @Test
    fun `create should save and return entity`() {
        `when`(repository.save(consumptionEntryEntity)).thenReturn(consumptionEntryEntity)

        val result = service.create(consumptionEntryEntity)

        assertEquals(consumptionEntryEntity, result)
        verify(repository).save(consumptionEntryEntity)
    }

    @Test
    fun `save should update existing entity`() {
        `when`(repository.findById(1L)).thenReturn(Optional.of(consumptionEntryEntity))
        `when`(repository.save(consumptionEntryEntity)).thenReturn(consumptionEntryEntity)

        val result = service.save(1L, consumptionEntryEntity)

        assertEquals(consumptionEntryEntity, result)
        verify(repository).save(consumptionEntryEntity)
    }

    @Test
    fun `save should throw exception when IDs don't match`() {
        val entityWithDifferentId = consumptionEntryEntity.copy(id = 2L) // Add other required fields

        assertThrows(EntityNotFoundException::class.java) {
            service.save(1L, entityWithDifferentId)
        }
    }

    @Test
    fun `getById should return entity when found`() {
        whenever(repository.findById(1L)).thenReturn(Optional.of(consumptionEntryEntity))

        val result = service.getById(1L)

        assertEquals(consumptionEntryEntity, result)
        verify(repository).findById(1L)
    }

    @Test
    fun `getById should throw EntityNotFoundException when not found`() {
        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            service.getById(1L)
        }
        verify(repository).findById(1L)
    }

    @Test
    fun `delete should remove entity when exists`() {
        whenever(repository.findById(1L)).thenReturn(Optional.of(consumptionEntryEntity))
        doNothing().whenever(repository).deleteById(1L)

        service.delete(1L)

        verify(repository).findById(1L)
        verify(repository).deleteById(1L)
    }

    @Test
    fun `delete should throw EntityNotFoundException when entity doesn't exist`() {
        whenever(repository.findById(1L)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            service.delete(1L)
        }
        verify(repository).findById(1L)
        verify(repository, never()).deleteById(1L)
    }
}