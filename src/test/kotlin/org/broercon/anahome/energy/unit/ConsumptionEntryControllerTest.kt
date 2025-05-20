package org.broercon.anahome.energy.consumptionentry

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.EnergyExceptionHandle
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.hamcrest.Matchers.hasSize
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
import java.time.LocalDateTime

@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@SpringBootTest(classes = [Application::class, ConsumptionEntryController::class])
@AutoConfigureMockMvc
class ConsumptionEntryControllerTest {

    @MockBean
    private lateinit var service: ConsumptionEntryService

    @MockBean
    private lateinit var mapper: ConsumptionEntryMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var consumptionEntryRest: ConsumptionEntryRest
    private lateinit var consumptionEntryEntity: ConsumptionEntryEntity
    private lateinit var meterUnitEntity: MeterUnitEntity

    @BeforeEach
    fun setup() {
        val now = LocalDateTime.now()

        meterUnitEntity = MeterUnitEntity(
            id = 1,
            label = "HT",
            unit = "kWh",
            meterEntity = MeterEntity(
                1,
                "Test Meter",
                "12345",
                "Test Location",
                MeterTypeEntity(1, "Strom")
            )
        )

        consumptionEntryRest = ConsumptionEntryRest(
            id = 1,
            timestamp = now,
            meterReading = 123.45,
            comment = "Test reading",
            meterUnitEntityId = 1
        )

        consumptionEntryEntity = ConsumptionEntryEntity(
            id = 1,
            timestamp = now,
            meterReading = 123.45,
            comment = "Test reading",
            meterUnitEntity = meterUnitEntity
        )

        // Setup default mapper behavior
        whenever(mapper.run { any<ConsumptionEntryEntity>().toRest() }).thenReturn(consumptionEntryRest)
        whenever(mapper.run { any<ConsumptionEntryRest>().toDomain() }).thenReturn(consumptionEntryEntity)
        whenever(mapper.run { any<List<ConsumptionEntryEntity>>().toRest() }).thenReturn(listOf(consumptionEntryRest))
    }

    @Test
    fun `getMeterTypes should return list of consumption entries`() {
        whenever(service.getAll()).thenReturn(listOf(consumptionEntryEntity))

        mockMvc.perform(get("/api/energy/consumptionentry"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize<Any>(1)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].meterReading").value(123.45))
            .andExpect(jsonPath("$[0].comment").value("Test reading"))
            .andExpect(jsonPath("$[0].meterUnitEntityId").value(1))
    }

    @Test
    fun `getMeterTypeById should return consumption entry when found`() {
        whenever(service.getById(eq(1))).thenReturn(consumptionEntryEntity)

        mockMvc.perform(get("/api/energy/consumptionentry/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.meterReading").value(123.45))
            .andExpect(jsonPath("$.comment").value("Test reading"))
            .andExpect(jsonPath("$.meterUnitEntityId").value(1))
    }

    @Test
    fun `getMeterTypeById should return 404 when consumption entry not found`() {
        whenever(service.getById(eq(999))).thenThrow(EntityNotFoundException("ConsumptionEntry not found"))

        mockMvc.perform(get("/api/energy/consumptionentry/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `create should return new consumption entry`() {
        whenever(service.create(any())).thenReturn(consumptionEntryEntity)

        mockMvc.perform(post("/api/energy/consumptionentry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionEntryRest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.meterReading").value(123.45))
            .andExpect(jsonPath("$.comment").value("Test reading"))
            .andExpect(jsonPath("$.meterUnitEntityId").value(1))
    }

    @Test
    fun `update should modify existing consumption entry`() {
        whenever(service.save(eq(1), any())).thenReturn(consumptionEntryEntity)

        mockMvc.perform(put("/api/energy/consumptionentry/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionEntryRest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.meterReading").value(123.45))
            .andExpect(jsonPath("$.comment").value("Test reading"))
            .andExpect(jsonPath("$.meterUnitEntityId").value(1))
    }

    @Test
    fun `update should return 404 when consumption entry not found`() {
        whenever(service.save(eq(999), any())).thenThrow(EntityNotFoundException("ConsumptionEntry not found"))

        mockMvc.perform(put("/api/energy/consumptionentry/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionEntryRest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `delete should return 202 when successful`() {
        doNothing().whenever(service).delete(eq(1))

        mockMvc.perform(delete("/api/energy/consumptionentry/1"))
            .andExpect(status().isAccepted)
    }

    @Test
    fun `delete should return 404 when consumption entry not found`() {
        doThrow(EntityNotFoundException("ConsumptionEntry not found")).whenever(service).delete(eq(999))

        mockMvc.perform(delete("/api/energy/consumptionentry/999"))
            .andExpect(status().isNotFound)
    }
}