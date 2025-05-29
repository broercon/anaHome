import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.EnergyExceptionHandle
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.broercon.anahome.energy.tariffplan.TariffPlanController
import org.broercon.anahome.energy.tariffplan.TariffPlanEntity
import org.broercon.anahome.energy.tariffplan.TariffPlanService
import org.hamcrest.Matchers.hasSize
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.test.Test

@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@SpringBootTest(classes = [Application::class, TariffPlanController::class])
@AutoConfigureMockMvc
class TariffPlanControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var tariffPlanService: TariffPlanService

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun `should create tariff plan`() {
        // Given
        val tariffPlan = TariffPlanEntity(
            name = "Test Plan",
            meterType = MeterTypeEntity(id = 1, name = "Test Type"),
            meter = null,
            effectiveFrom = LocalDateTime.of(2024, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2024, 1, 31, 23, 59),
            id = 1,
            meterUnit = null
        )

        whenever(tariffPlanService.create(any())).thenReturn(tariffPlan)

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tariffPlan))
        )
            // Then
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Plan"))
            .andExpect(jsonPath("$.effectiveFrom").exists())
            .andExpect(jsonPath("$.effectiveTo").exists())
    }

    @Test
    fun `should get tariff plan by id`() {
        // Given
        val tariffPlan = TariffPlanEntity(
            id = 1,
            name = "Test Plan",
            meterType = MeterTypeEntity(id = 1, name = "Test Type"),
            meter = null,
            effectiveFrom = LocalDateTime.of(2024, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2024, 1, 31, 23, 59),
            meterUnit = null
        )

        whenever(tariffPlanService.getById(1)).thenReturn(tariffPlan)

        // When
        mockMvc.perform(get("/api/energy/tariffplan/1"))
            // Then
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Plan"))
    }

    @Test
    fun `should return 404 when tariff plan not found`() {
        // Given
        whenever(tariffPlanService.getById(999)).thenThrow(EntityNotFoundException("Tariff plan not found"))

        // When
        mockMvc.perform(get("/api/energy/tariffplan/999"))
            // Then
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should get all tariff plans`() {
        // Given
        val tariffPlans = listOf(
            TariffPlanEntity(
                id = 1,
                name = "Plan 1",
                meterType = MeterTypeEntity(id = 1, name = "Type 1"),
                meter = null,
                effectiveFrom = LocalDateTime.of(2024, 1, 1, 0, 0),
                effectiveTo = LocalDateTime.of(2024, 1, 31, 23, 59),
                meterUnit = null
            ),
            TariffPlanEntity(
                id = 2,
                name = "Plan 2",
                meterType = MeterTypeEntity(id = 1, name = "Type 1"),
                meter = null,
                effectiveFrom = LocalDateTime.of(2024, 2, 1, 0, 0),
                effectiveTo = LocalDateTime.of(2024, 2, 28, 23, 59),
                meterUnit = null
            )
        )

        whenever(tariffPlanService.getAll()).thenReturn(tariffPlans)

        // When
        mockMvc.perform(get("/api/energy/tariffplan"))
            // Then
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2))
    }

    @Test
    fun `should update tariff plan`() {
        // Given
        val updatedTariffPlan = TariffPlanEntity(
            id = 1,
            name = "Updated Plan",
            meterType = MeterTypeEntity(id = 1, name = "Test Type"),
            meter = null,
            effectiveFrom = LocalDateTime.of(2024, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2024, 1, 31, 23, 59),
            meterUnit = null
        )

        whenever(tariffPlanService.save(eq(1), any())).thenReturn(updatedTariffPlan)

        // When
        mockMvc.perform(
            put("/api/energy/tariffplan/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedTariffPlan))
        )
            // Then
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Plan"))
    }

    @Test
    fun `should delete tariff plan`() {
        // Given
        doNothing().whenever(tariffPlanService).delete(1)

        // When
        mockMvc.perform(delete("/api/energy/tariffplan/1"))
            // Then
            .andExpect(status().isAccepted)
    }

    @Test
    fun `should validate tariff plan creation with invalid dates`() {
        // Given
        val invalidTariffPlan = TariffPlanEntity(
            name = "Invalid Plan",
            meterType = MeterTypeEntity(id = 1, name = "Test Type"),
            meter = null,
            effectiveFrom = LocalDateTime.of(2024, 1, 31, 0, 0),
            effectiveTo = LocalDateTime.of(2024, 1, 1, 0, 0),
            id = 1 ,
            meterUnit = null    // Invalid: end before start
        )

        whenever(tariffPlanService.create(any())).thenThrow(IllegalArgumentException("effectiveFrom must be before effectiveTo"))

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidTariffPlan))
        )
            // Then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should handle overlapping tariff plans`() {
        // Given
        val overlappingPlan = TariffPlanEntity(
            name = "Overlapping Plan",
            meterType = MeterTypeEntity(id = 1, name = "Test Type"),
            meter = null,
            effectiveFrom = LocalDateTime.of(2024, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2024, 1, 31, 23, 59),
            id = 1,
            meterUnit = null
        )

        whenever(tariffPlanService.create(any())).thenThrow(IllegalArgumentException("New tariff plan overlaps with existing plan"))

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(overlappingPlan))
        )
            // Then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should validate required fields`() {
        // Given
        val invalidTariffPlan = TariffPlanEntity(
            name = "",  // Empty name
            meterType = null,  // No meter type
            meter = null,      // No meter
            effectiveFrom = LocalDateTime.of(2024, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2024, 1, 31, 23, 59),
            id = 1,
            meterUnit = null
        )

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(invalidTariffPlan))
        )
            // Then
            .andExpect(status().isBadRequest)

        // name is required
        var json = """
                    {
                      "id": 1,
                      "name": "",
                      "meterType": null,
                      "meter": null,
                      "effectiveFrom": null
                      "effectiveTo": "2024-01-31T23:59:00",
                      "meterUnit": null
                    }
                    """.trimIndent()

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            // Then
            .andExpect(status().isBadRequest)

        // effectiveFrom is required
        json = """
                    {
                      "id": 1,
                      "name": "name",
                      "meterType": null,
                      "meter": null,
                      "effectiveFrom": null
                      "effectiveTo": "2024-01-31T23:59:00",
                      "meterUnit": null
                    }
                    """.trimIndent()

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            // Then
            .andExpect(status().isBadRequest)

        // effectiveFrom is required
        json = """
                    {
                      "id": 1,
                      "name": "name",
                      "meterType": null,
                      "meter": null,
                      "effectiveTo": "2024-01-31T23:59:00",
                      "meterUnit": null
                    }
                    """.trimIndent()

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            // Then
            .andExpect(status().isBadRequest)

        // effectiveTo bevor effectiveFrom
        json = """
                    {
                      "id": 1,
                      "name": "name",
                      "meterType": null,
                      "meter": null,
                      "effectiveFrom": "2024-01-31T23:59:00"
                      "effectiveTo": "2024-01-01T23:59:00",
                      "meterUnit": null
                    }
                    """.trimIndent()

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            // Then
            .andExpect(status().isBadRequest)

        // corrupt json
        json = """
                      "meter": null,
                      "effectiveFrom": "2024-01-31T23:59:00"
                      "effectiveTo": "2024-01-01T23:59:00",
                      "meterUnit": null
                    }
                    """.trimIndent()

        // When
        mockMvc.perform(
            post("/api/energy/tariffplan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            // Then
            .andExpect(status().isBadRequest)
    }
}