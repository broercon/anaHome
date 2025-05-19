package org.broercon.anahome.energy

import org.broercon.anahome.Application
import org.broercon.anahome.energy.consumptionentry.ConsumptionEntryEntity
import org.broercon.anahome.energy.consumptionentry.ConsumptionEntryRepository
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meter.MeterRepository
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitRepository
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.broercon.anahome.energy.metertype.MeterTypeRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime
import kotlin.test.Test

@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@SpringBootTest(classes = [Application::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MeterIntegrationTest {

    @Autowired
    lateinit var meterTypeRepository: MeterTypeRepository

    @Autowired
    lateinit var meterRepository: MeterRepository

    @Autowired
    lateinit var meterUnit: MeterUnitRepository

    @Autowired
    lateinit var consumptionEntry: ConsumptionEntryRepository

    @BeforeAll
    fun cleanupBefore() {
        consumptionEntry.deleteAll()
        meterUnit.deleteAll()
        meterRepository.deleteAll()
        meterTypeRepository.deleteAll()
    }

    @Test
    fun `create meter with metertype`() {
        // Create and save a MeterType
        val meterType = meterTypeRepository.save(
            MeterTypeEntity(id = 1, name = "Electricity"
            ))

        // Assertions
        Assertions.assertNotNull(meterType.id)
        Assertions.assertEquals("Electricity", meterType.name)

        // Create and save a Meter
        val meter = meterRepository.save(
            MeterEntity(
                id = 1,
                name = "Main meter",
                meterNumber = "ZN64538",
                location = "Keller",
                meterTypeEntity = meterType
            ))

        // Assertions
        Assertions.assertNotNull(meter.id)
        Assertions.assertEquals("Main meter", meter.name)
        Assertions.assertEquals("Electricity", meter.meterTypeEntity!!.name)

        val meterUnit = meterUnit.save(MeterUnitEntity(
            id = 1,
            label = "HT",
            unit = "kWh",
            meterEntity = meter
        ))

        // Assertions
        Assertions.assertNotNull(meter.id)
        Assertions.assertEquals("HT", meterUnit.label)
        Assertions.assertEquals("Main meter", meterUnit.meterEntity!!.name)
        Assertions.assertEquals("Electricity", meterUnit.meterEntity.meterTypeEntity!!.name)

        val consumption = consumptionEntry.save(ConsumptionEntryEntity(
            id = 1,
            timestamp = LocalDateTime.now(),
            meterReading = 200.0,
            comment = "Start of month",
            meterUnitEntity = meterUnit
        ))

        // Assertions
        Assertions.assertNotNull(meter.id)
        Assertions.assertEquals(200.0, consumption.meterReading)
        Assertions.assertEquals("HT", consumption.meterUnitEntity.label)
        Assertions.assertEquals("Main meter", consumption.meterUnitEntity.meterEntity!!.name)
        Assertions.assertEquals("Electricity", consumption.meterUnitEntity.meterEntity.meterTypeEntity!!.name)
    }
}