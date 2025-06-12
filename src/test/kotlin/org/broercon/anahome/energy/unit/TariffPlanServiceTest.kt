import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meter.MeterRepository
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitRepository
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.broercon.anahome.energy.metertype.MeterTypeRepository
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import org.broercon.anahome.energy.tariffplan.TariffPlanRepository
import org.broercon.anahome.energy.tariffplan.TariffPlanService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Import(TestContainersConfig::class)
@SpringBootTest(classes = [Application::class])
class TariffPlanServiceTest {

    @Autowired
    private lateinit var tariffPlanService: TariffPlanService

    @Autowired
    private lateinit var tariffPlanRepository: TariffPlanRepository

    @Autowired
    private lateinit var meterTypeRepository: MeterTypeRepository

    @Autowired
    private lateinit var meterRepository: MeterRepository

    @Autowired
    private lateinit var meterUnitRepository: MeterUnitRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var testMeterType: MeterTypeEntity
    private lateinit var testMeter: MeterEntity
    private lateinit var testMeterUnit: MeterUnitEntity

    @BeforeEach
    fun setup() {
        // Clean up database
        tariffPlanRepository.deleteAll()
        jdbcTemplate.execute("TRUNCATE TABLE tariff_plans RESTART IDENTITY CASCADE")
        
        // Create testdata
        testMeterType = meterTypeRepository.save(MeterTypeEntity(name = "Test Electricity", id = 1))
        testMeter = meterRepository.save(MeterEntity(name = "Test Meter", location = "Test Location", id = 1, meterNumber = "ZN213", meterTypeEntity = testMeterType))
        testMeterUnit = meterUnitRepository.save(MeterUnitEntity(label = "HT", unit = "kWh", meterEntity = testMeter, id = 1))
    }

    @Test
    fun `should create tariff plan`() {
        var tariffPlan = TariffPlanEntity(
            name = "Test Plan",
            meterType = testMeterType,
            meter = null,
            effectiveFrom = LocalDateTime.now(),
            id = 1,
            effectiveTo = null,
            meterUnit = null
        )
        val saved = tariffPlanService.create(tariffPlan)
        
        assertNotNull(saved.id)
        assertEquals(tariffPlan.name, saved.name)

        tariffPlan.id = 2
        tariffPlan.meter = testMeter
        tariffPlan.meterType = null

        assertNotNull(saved.id)
        assertEquals(tariffPlan.name, saved.name)

        tariffPlan.id = 2
        tariffPlan.meter = null
        tariffPlan.meterUnit = testMeterUnit

        assertNotNull(saved.id)
        assertEquals(tariffPlan.name, saved.name)
    }

    @Test
    fun `should not create tariff plan`() {
        val tariffPlan = TariffPlanEntity(
            name = "Test Plan",
            meterType = testMeterType,
            meter = testMeter,
            effectiveFrom = LocalDateTime.now(),
            id = 1,
            effectiveTo = null,
            meterUnit = testMeterUnit
        )

        val exception = assertThrows<IllegalArgumentException> {
            tariffPlanService.create(tariffPlan)
        }

        // Optionally verify the exception message
        assertEquals("Exactly one meter must be set!", exception.message)
    }

    @Test
    fun `should get all tariff plans`() {
        // Create multiple tariff plans
        tariffPlanService.create(TariffPlanEntity(name = "Test Plan1", meterType = testMeterType, meter = null, effectiveFrom = LocalDateTime.now(), id = 1, effectiveTo = null, meterUnit = null))

        tariffPlanService.create(TariffPlanEntity(name = "Test Plan2", meterType = null, meter = testMeter, effectiveFrom = LocalDateTime.now(), id = 2, effectiveTo = null, meterUnit = null))

        val allPlans = tariffPlanService.getAll()
        
        assertEquals(2, allPlans.size)
        assertTrue(allPlans.any { it?.name == "Test Plan1" })
        assertTrue(allPlans.any { it?.name == "Test Plan2" })
    }

    @Test
    fun `should find active tariff plans`() {
        val now = LocalDateTime.now()
        val yesterday = now.minusDays(1)
        val tomorrow = now.plusDays(1)

        // Create active plan
        val activePlan = tariffPlanService.create(
            TariffPlanEntity(name = "Active Plan", meterType = testMeterType, meter = null, effectiveFrom = yesterday, effectiveTo = tomorrow, id = 1, meterUnit = null))

        // Create expired plan
        val expiredPlan = tariffPlanService.create(
            TariffPlanEntity(name = "Expired Plan", meterType = null, meter = testMeter, effectiveFrom = yesterday.minusDays(2), effectiveTo = yesterday, id = 2, meterUnit = null))

        val activePlans = tariffPlanService.findActivePlans()
        
        assertTrue(activePlans.contains(activePlan))
        assertFalse(activePlans.contains(expiredPlan))
    }

    @Test
    fun `should update tariff plan`() {
        val original = tariffPlanService.create(TariffPlanEntity(
            name = "Original Name",
            meterType = testMeterType,
            meter = null,
            effectiveFrom = LocalDateTime.now(),
            id = 0,
            effectiveTo = null,
            meterUnit = null
        ))

        val updated = original.copy(name = "Updated Name")
        val result = tariffPlanService.save(id = updated.id, updated)

        assertEquals("Updated Name", result.name)
        assertEquals(original.id, result.id)
    }

    @Test
    fun `should delete tariff plan`() {
        val plan = tariffPlanService.create(TariffPlanEntity(name = "To Delete", meterType = testMeterType, meter = null, effectiveFrom = LocalDateTime.now(), id = 1, effectiveTo = null, meterUnit = null))

        tariffPlanService.delete(plan.id!!)

        val exception = assertThrows<EntityNotFoundException> {
            tariffPlanService.getById(plan.id!!)
        }

        // Optionally verify the exception message
        assertEquals("TariffPlan with id 1 not found", exception.message)
    }

    @Test
    fun `should not allow overlapping date ranges for same meter type`() {
        val now = LocalDateTime.now()

        // Create first plan
        tariffPlanService.create(TariffPlanEntity(
            name = "First Plan",
            meterType = testMeterType,
            meter = null,
            effectiveFrom = now,
            effectiveTo = now.plusDays(10),
            id = 1,
            meterUnit = null
        ))

        // Try to create overlapping plan
        val exception = assertThrows<IllegalStateException> {
            tariffPlanService.create(TariffPlanEntity(
                name = "Overlapping Plan",
                meterType = testMeterType,
                meter = null,
                effectiveFrom = now.plusDays(5),
                effectiveTo = now.plusDays(15),
                id = 2,
                meterUnit = null
            ))
        }

        // Optionally verify the exception message
        assertEquals("New tariff plan overlaps with existing plan", exception.message)
    }
}