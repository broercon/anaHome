import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.EnergyExceptionHandle
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import org.broercon.anahome.energy.tariffrate.TariffRateController
import org.broercon.anahome.energy.tariffrate.TariffRateEntity
import org.broercon.anahome.energy.tariffrate.TariffRateMapper
import org.broercon.anahome.energy.tariffrate.TariffRateRest
import org.broercon.anahome.energy.tariffrate.TariffRateService
import org.hamcrest.Matchers.hasSize
import org.hibernate.type.descriptor.DateTimeUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@SpringBootTest(classes = [Application::class, TariffRateController::class])
@AutoConfigureMockMvc
class TariffRateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var tariffRateService: TariffRateService

    @MockBean
    private lateinit var mapper: TariffRateMapper

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var tariffRateRest: TariffRateRest
    private lateinit var tariffRateEntity: TariffRateEntity

    @BeforeEach
    fun setup() {
        val tariffPlan = TariffPlanEntity(
            id = 1,
            name = "Test Plan",
            effectiveFrom = LocalDateTime.now(),
            effectiveTo = LocalDateTime.now().plusMonths(1)
        )

        tariffRateEntity = TariffRateEntity(
            id = 1L,
            tariffPlan = tariffPlan,
            unitPrice = 10.50.toBigDecimal(),
            effectiveFrom = LocalDateTime.now(),
            effectiveTo = LocalDateTime.now().plusMonths(1),
        )

        tariffRateRest = TariffRateRest(
            id = 1L,
            tariffPlanId = 1,
            unitPrice = 10.50.toBigDecimal(),
            effectiveFrom = LocalDateTime.now(),
            effectiveTo = LocalDateTime.now().plusMonths(1),
        )

        // Setup default mapper behavior
        whenever(mapper.run { any<TariffRateEntity>().toRest() }).thenReturn(tariffRateRest)
        whenever(mapper.run { any<TariffRateRest>().toDomain() }).thenReturn(tariffRateEntity)
        whenever(mapper.run { any<List<TariffRateEntity>>().toRest() }).thenReturn(listOf(tariffRateRest))
    }

    @Test
    fun `should get all tariff rates`() {
        whenever(tariffRateService.getAll()).thenReturn(listOf(tariffRateEntity))

        mockMvc.perform(get("/api/energy/tariffrate"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].unitPrice").value(10.50))
            .andExpect(jsonPath("$[0].tariffPlanId").value(1))
    }

    @Test
    fun `should get all tariff rates by plan id`() {
        whenever(tariffRateService.getAllByTariffPlan(1)).thenReturn(listOf(tariffRateEntity))

        mockMvc.perform(get("/api/energy/tariffrate/plan/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].unitPrice").value(10.50))
    }

    @Test
    fun `should get tariff rate by id`() {
        whenever(tariffRateService.getById(1)).thenReturn(tariffRateEntity)

        mockMvc.perform(get("/api/energy/tariffrate/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.unitPrice").value(10.50))
    }

    @Test
    fun `should return 404 when tariff rate not found`() {
        whenever(tariffRateService.getById(999))
            .thenThrow(EntityNotFoundException("TariffRate not found"))

        mockMvc.perform(get("/api/energy/tariffrate/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create new tariff rate`() {
        whenever(tariffRateService.create(any())).thenReturn(tariffRateEntity)

        mockMvc.perform(
            post("/api/energy/tariffrate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tariffRateRest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.unitPrice").value(10.50))
    }

    @Test
    fun `should update existing tariff rate`() {
        whenever(tariffRateService.save(eq(1), any())).thenReturn(tariffRateEntity)

        mockMvc.perform(
            put("/api/energy/tariffrate/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tariffRateRest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.unitPrice").value(10.50))
    }

    @Test
    fun `should return 404 when updating non-existent tariff rate`() {
        whenever(tariffRateService.save(eq(999), any()))
            .thenThrow(EntityNotFoundException("TariffRate not found"))

        mockMvc.perform(
            put("/api/energy/tariffrate/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tariffRateRest))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should delete tariff rate`() {
        doNothing().whenever(tariffRateService).delete(1)

        mockMvc.perform(delete("/api/energy/tariffrate/1"))
            .andExpect(status().isAccepted)
    }

    @Test
    fun `should return 404 when deleting non-existent tariff rate`() {
        doThrow(EntityNotFoundException("TariffRate not found"))
            .whenever(tariffRateService).delete(999)

        mockMvc.perform(delete("/api/energy/tariffrate/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should validate time range`() {
        val invalidTimeRange = tariffRateRest.copy(
            effectiveTo = LocalDateTime.now(),
            effectiveFrom = LocalDateTime.now().plusDays(180)
        )

        doThrow(IllegalArgumentException("Effective from date must be after effective from date of tariff plan!"))
            .whenever(tariffRateService).create(any())

        mockMvc.perform(
            post("/api/energy/tariffrate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTimeRange))
        )
            .andExpect(status().isBadRequest)
    }
}