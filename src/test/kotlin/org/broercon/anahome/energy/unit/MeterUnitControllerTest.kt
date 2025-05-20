import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.EnergyExceptionHandle
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.meterUnit.MeterUnitController
import org.broercon.anahome.energy.meterUnit.MeterUnitEntity
import org.broercon.anahome.energy.meterUnit.MeterUnitRest
import org.broercon.anahome.energy.meterUnit.MeterUnitService
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
@SpringBootTest(classes = [Application::class, MeterUnitController::class])
@AutoConfigureMockMvc
class MeterUnitControllerTest {

    @MockBean
    private lateinit var service: MeterUnitService

    @MockBean
    private lateinit var meterService: MeterService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var meterUnitRest: MeterUnitRest
    private lateinit var meterUnitEntity: MeterUnitEntity
    private lateinit var meterUnitEntity2: MeterUnitEntity
    private lateinit var meterEntity: MeterEntity

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

        meterUnitRest = MeterUnitRest(
            id = 1,
            label = "HT",
            unit = "kWh",
            meterEntityId = 1
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
    fun `getAll should return list of meter units`() {
        whenever(service.getAll()).thenReturn(listOf(meterUnitEntity, meterUnitEntity2))

        mockMvc.perform(get("/api/energy/meterunit"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].label").value("HT"))
            .andExpect(jsonPath("$[1].label").value("NT"))
            .andExpect(jsonPath("$[0].meterEntityId").value(1))
    }

    @Test
    fun `getById should return meter unit when found`() {
        whenever(service.getById(eq(1))).thenReturn(meterUnitEntity)

        mockMvc.perform(get("/api/energy/meterunit/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.label").value("HT"))
            .andExpect(jsonPath("$.meterEntityId").value(1))
    }

    @Test
    fun `getById should return 404 when meter unit not found`() {
        whenever(service.getById(eq(999))).thenThrow(EntityNotFoundException("MeterUnit not found"))

        mockMvc.perform(get("/api/energy/meterunit/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `create should return new meter unit`() {
        whenever(service.create(any())).thenReturn(meterUnitEntity)

        mockMvc.perform(post("/api/energy/meterunit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterUnitRest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.label").value("HT"))
            .andExpect(jsonPath("$.meterEntityId").value(1))
    }

    @Test
    fun `update should modify existing meter unit`() {
        whenever(service.save(eq(1), any())).thenReturn(meterUnitEntity)

        mockMvc.perform(put("/api/energy/meterunit/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterUnitRest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.label").value("HT"))
            .andExpect(jsonPath("$.meterEntityId").value(1))
    }

    @Test
    fun `update should return 404 when meter unit not found`() {
        whenever(service.save(eq(999), any())).thenThrow(EntityNotFoundException("MeterUnit not found"))

        mockMvc.perform(put("/api/energy/meterunit/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterUnitRest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `delete should return 204 when successful`() {
        doNothing().whenever(service).delete(eq(1))

        mockMvc.perform(delete("/api/energy/meterunit/1"))
            .andExpect(status().isAccepted)
    }

    @Test
    fun `delete should return 404 when meter unit not found`() {
        doThrow(EntityNotFoundException("MeterUnit not found")).whenever(service).delete(eq(999))

        mockMvc.perform(delete("/api/energy/meterunit/999"))
            .andExpect(status().isNotFound)
    }
}