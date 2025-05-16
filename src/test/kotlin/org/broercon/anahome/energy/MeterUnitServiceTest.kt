package org.broercon.anahome.energy.meterunit

import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitRepository
import org.broercon.anahome.energy.meterUnit.MeterUnitService
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class MeterUnitServiceTest {

    @Mock
    private lateinit var repository: MeterUnitRepository

    @Mock
    private lateinit var meterService: MeterService

    @InjectMocks
    private lateinit var service: MeterUnitService

    @Captor
    private lateinit var entityCaptor: ArgumentCaptor<MeterUnitEntity>

    private lateinit var meterEntity: MeterEntity
    private lateinit var meterUnitEntity: MeterUnitEntity
    private lateinit var meterUnitEntity2: MeterUnitEntity

    @BeforeEach
    fun setup() {
        val now = LocalDateTime.now()

        meterEntity = MeterEntity(
            id = 1,
            name = "Test Meter",
            meterNumber = "12345",
            location = "Test Location",
            meterTypeEntity = MeterTypeEntity(1, "Strom")
        )

        meterUnitEntity = MeterUnitEntity(
            id = 1,
            label = "HT",
            unit = "kWh",
            meterEntity = meterEntity
        )

        meterUnitEntity2 = MeterUnitEntity(
            id = 2,
            label = "NT",
            unit = "kWh",
            meterEntity = meterEntity
        )
    }

    @Test
    fun `getAll returns all meter units`() {
        whenever(repository.findAll()).thenReturn(listOf(meterUnitEntity, meterUnitEntity2))

        val result = service.getAll()

        assertEquals(2, result.size)
        assertEquals(meterUnitEntity, result[0])
        assertEquals(meterUnitEntity2, result[1])
        verify(repository).findAll()
    }

    @Test
    fun `getById returns meter unit when found`() {
        whenever(repository.findById(1)).thenReturn(Optional.of(meterUnitEntity))

        val result = service.getById(1)

        assertEquals(meterUnitEntity, result)
        verify(repository).findById(1)
    }

    @Test
    fun `getById throws EntityNotFoundException when not found`() {
        whenever(repository.findById(999)).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            service.getById(999)
        }

        assertEquals("MeterUnit with id 999 not found", exception.message)
        verify(repository).findById(999)
    }

    @Test
    fun `create saves new meter unit`() {
        whenever(repository.save(meterUnitEntity)).thenReturn(meterUnitEntity)

        val newMeterUnit = meterUnitEntity

        val result = service.create(newMeterUnit)

        verify(repository).save(capture(entityCaptor))
        assertEquals(newMeterUnit.label, entityCaptor.value.label)
        assertEquals(newMeterUnit.meterEntity, entityCaptor.value.meterEntity)
        assertEquals(meterUnitEntity, result)
    }

    @Test
    fun `save updates existing meter unit`() {
        whenever(repository.findById(1)).thenReturn(Optional.of(meterUnitEntity))
        whenever(repository.save(any())).thenReturn(meterUnitEntity.copy(label="ST"))

        val updatedMeterUnit = meterUnitEntity.copy(label = "ST")
        val result = service.save(1, updatedMeterUnit)

        verify(repository).save(capture(entityCaptor))
        assertEquals("ST", entityCaptor.value.label)
        assertEquals(updatedMeterUnit.meterEntity, entityCaptor.value.meterEntity)
        assertEquals("ST", result.label)
    }

    @Test
    fun `save throws Exception when meter unit ID not equal EntityID`() {
        val exception = assertThrows<EntityNotFoundException> {
            service.save(999, meterUnitEntity)
        }

        assertEquals("ID does not match the transferred data record", exception.message)
        verify(repository, never()).save(any())
    }

    @Test
    fun `save throws EntityNotFoundException when meter unit not found`() {
        whenever(repository.findById(999)).thenReturn(Optional.empty())

        var newMeterUnit = meterUnitEntity
        newMeterUnit.id=999
        val exception = assertThrows<EntityNotFoundException> {

            service.save(999, newMeterUnit)
        }

        assertEquals("MeterUnit with id 999 not found", exception.message)
        verify(repository, never()).save(any())
    }

    @Test
    fun `delete removes meter unit when exists`() {
        whenever(repository.findById(1)).thenReturn(Optional.of(meterUnitEntity))
        doNothing().whenever(repository).deleteById(1)

        service.delete(1)

        verify(repository).findById(1)
        verify(repository).deleteById(1)
    }

    @Test
    fun `delete throws EntityNotFoundException when meter unit not found`() {
        whenever(repository.findById(999)).thenReturn(Optional.empty())

        val exception = assertThrows<EntityNotFoundException> {
            service.delete(999)
        }

        assertEquals("MeterUnit with id 999 not found", exception.message)
        verify(repository, never()).deleteById(any())
    }
}