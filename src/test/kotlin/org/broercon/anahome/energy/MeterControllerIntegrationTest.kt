import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.EnergyExceptionHandle
import org.broercon.anahome.energy.meter.MeterController
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meter.MeterRest
import org.broercon.anahome.energy.meter.MeterService
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


@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@SpringBootTest(classes = [Application::class, MeterController::class])
@AutoConfigureMockMvc
class MeterControllerTest {

    @MockBean
    private lateinit var service: MeterService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var meterRest: MeterRest
    private lateinit var meterEntity: MeterEntity
    private lateinit var meterEntity2: MeterEntity

    @BeforeEach
    fun setup() {
        
        meterRest = MeterRest(
            id = 1,
            name = "Test Meter",
            meterNumber = "12345",
            location = "Test Location",
            meterTypeEntityId = 1
        )

        meterEntity = MeterEntity(
            id = 1,
            name = "Test Meter",
            meterNumber = "12345",
            location = "Test Location",
            meterTypeEntity = MeterTypeEntity(1, "Strom")
        )

        meterEntity2 = MeterEntity(
            id = 2,
            name = "Test Meter2",
            meterNumber = "6789",
            location = "Location 2",
            meterTypeEntity = MeterTypeEntity(2, "Gas")
        )
    }

    @Test
    fun `getAll should return list of meters`() {
        // Given
        whenever(service.getAll()).thenReturn(listOf(meterEntity, meterEntity2))

        // When/Then
        mockMvc.perform(get("/api/energy/meter"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize<Any>(2)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Test Meter"))
            .andExpect(jsonPath("$[0].meterNumber").value("12345"))
            .andExpect(jsonPath("$[0].location").value("Test Location"))
            .andExpect(jsonPath("$[0].meterTypeEntityId").value(1))
    }

    @Test
    fun `getById should return meter when found`() {
        // Given
        whenever(service.getById(1)).thenReturn(meterEntity)

        // When/Then
        mockMvc.perform(get("/api/energy/meter/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Meter"))
    }

    @Test
    fun `getById should return 404 when meter not found`() {
        // Given
        whenever(service.getById(999)).thenThrow(EntityNotFoundException("MeterType with id 999 not found"))

        // When/Then
        mockMvc.perform(get("/api/energy/meter/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `create should return new meter`() {
        // Given

        whenever(service.create(any())).thenReturn(meterEntity)

        // When/Then
        mockMvc.perform(post("/api/energy/meter")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterRest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Meter"))
    }

    @Test
    fun `update should modify existing meter`() {
        // Given
        whenever(service.save(eq(1), any())).thenReturn(meterEntity)

        // When/Then
        mockMvc.perform(put("/api/energy/meter/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterRest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Meter"))
    }

    @Test
    fun `update should return 404 when meter not found`() {
        // Given
        whenever(service.save(eq(1), any())).thenThrow(EntityNotFoundException("Meter not found"))

        // When/Then
        mockMvc.perform(put("/api/energy/meter/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterRest)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `delete should return 204 when successful`() {
        // Given
        doNothing().whenever(service).delete(1)

        // When/Then
        mockMvc.perform(delete("/api/energy/meter/1"))
            .andExpect(status().isAccepted)
    }

    @Test
    fun `delete should return 404 when meter not found`() {
        // Given
        doThrow(EntityNotFoundException("Meter not found"))
            .whenever(service).delete(1)
        // When/Then
        mockMvc.perform(delete("/api/energy/meter/1"))
            .andExpect(status().isNotFound)
    }
}