import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.EnergyExceptionHandle
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import org.broercon.anahome.energy.tariffrate.TariffRateEntity
import org.broercon.anahome.energy.tariffrate.TariffRateRepository
import org.broercon.anahome.energy.tariffrate.TariffRateService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional
import kotlin.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@ExtendWith(MockitoExtension::class)
class TariffRateServiceTest {
    
    @Mock
    private lateinit var tariffRateRepository: TariffRateRepository

    @InjectMocks
    private lateinit var tariffRateService: TariffRateService

    private lateinit var testTariffPlan: TariffPlanEntity
    private lateinit var testRate: TariffRateEntity

    @BeforeEach
    fun setUp() {
        testTariffPlan = TariffPlanEntity(
            id = 1L,
            name = "Test Plan",
            effectiveFrom = LocalDateTime.now(),
            effectiveTo = LocalDateTime.now().plusMonths(1),
            meterType = MeterTypeEntity(1L, "Test Type")
        )

        testRate = TariffRateEntity(
            id = 1L,
            tariffPlan = testTariffPlan,
            unitPrice = 0.65,
            effectiveFrom = LocalDateTime.now(),
            unit = "GP",
            effectiveTo = LocalDateTime.now().plusMonths(1),
        )
    }

    @Test
    fun `should create tariff rate`() {
        // Given
        whenever(tariffRateRepository.save(any())).thenReturn(testRate)

        // When
        val result = tariffRateService.create(testRate)

        // Then
        assertNotNull(result)
        assertEquals(testRate.unitPrice, result.unitPrice)
        verify(tariffRateRepository).save(any())
    }

    @Test
    fun `should throw exception when creating rate with invalid time range`() {
        // Given
        val invalidRate = testRate.copy(
            effectiveFrom = LocalDateTime.now().plusMonths(3),
            effectiveTo = LocalDateTime.now().minusMinutes(180)
        )

        // When/Then
        assertThrows<IllegalArgumentException> {
            tariffRateService.create(invalidRate)
        }
    }

    @Test
    fun `should throw exception when rate overlaps with existing rate`() {
        // Given
        val existingRate = testRate.copy(
            effectiveFrom = LocalDateTime.now().minusMinutes(180),
            effectiveTo = LocalDateTime.now().plusMonths(3)
        )
        
        whenever(tariffRateService.getAllByTariffPlan(any())).thenReturn(listOf(existingRate))

        val newRate = testRate.copy(
            id = null,
            effectiveFrom = LocalDateTime.now().minusMinutes(100),
            effectiveTo = LocalDateTime.now().plusMonths(4))

        // When/Then
        assertThrows<IllegalStateException> {
                tariffRateService.create(newRate)
            }
    }

    @Test
    fun `should get tariff rate by id`() {
        // Given
        whenever(tariffRateRepository.findById(1L)).thenReturn(Optional.of(testRate))

        // When
        val result = tariffRateService.getById(1L)

        // Then
        assertNotNull(result)
        assertEquals(testRate.id, result.id)
    }

    @Test
    fun `should throw exception when tariff rate not found`() {
        // Given
        whenever(tariffRateRepository.findById(999L)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<EntityNotFoundException> {
            tariffRateService.getById(999L)
        }
    }

    @Test
    fun `should update tariff rate`() {
        // Given
        val updatedRate = testRate.copy(unitPrice = 15.75)
        whenever(tariffRateRepository.findById(1L)).thenReturn(Optional.of(testRate))
        whenever(tariffRateRepository.save(any())).thenReturn(updatedRate)
        whenever(tariffRateRepository.findByTariffPlanId(any()))
            .thenReturn(emptyList())

        // When
        val result = tariffRateService.save(1,updatedRate)

        // Then
        assertNotNull(result)
        assertEquals(updatedRate.unitPrice, result.unitPrice)
    }

    @Test
    fun `should delete tariff rate`() {
        // Given
        whenever(tariffRateRepository.findById(1L)).thenReturn(Optional.of(testRate))
        doNothing().whenever(tariffRateRepository).deleteById(1L)

        // When
        tariffRateService.delete(1L)

        // Then
        verify(tariffRateRepository).deleteById(1L)
    }

    @Test
    fun `should get all rates for tariff plan`() {
        // Given
        val rates = listOf(
            testRate,
            testRate.copy(
                id = 2L,
                effectiveFrom = LocalDateTime.now().minusMinutes(100),
                effectiveTo = LocalDateTime.now().plusMonths(4))
            )

        whenever(tariffRateRepository.findByTariffPlanId(1)).thenReturn(rates)

        // When
        val result = tariffRateService.getAllByTariffPlan(1L)

        // Then
        assertEquals(2, result.size)
        verify(tariffRateRepository).findByTariffPlanId(1L)
    }

    @Test
    fun `should validate tariff plan is not null`() {
        // Given
        val invalidRate = testRate.copy(tariffPlan = null)

        // When/Then
        assertThrows<IllegalArgumentException> {
            tariffRateService.create(invalidRate)
        }
    }
}