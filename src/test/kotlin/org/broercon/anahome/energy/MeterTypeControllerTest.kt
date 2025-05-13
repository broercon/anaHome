import org.anaHome.org.broercon.anahome.energy.metertype.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.EntityNotFoundException
import org.anaHome.org.broercon.anahome.Application
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestContainersConfig::class, EnergyExceptionHandle::class)
@SpringBootTest(classes = [Application::class, MeterTypeController::class])
@AutoConfigureMockMvc
class MeterTypeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var meterTypeService: MeterTypeService


    @Test
    fun `should get all meter types`() {
        // Given
        val meterTypes = listOf(
            MeterTypeEntity(id = 1, name = "Gas"),
            MeterTypeEntity(id = 2, name = "Electric")
        )
        whenever(meterTypeService.getAll()).thenReturn(meterTypes)

        // When/Then
        mockMvc.perform(get("/api/energy/metertype"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Gas"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Electric"))
    }

    @Test
    fun `should get meter type by id`() {
        // Given
        val meterType = MeterTypeEntity(id = 1, name = "Gas")
        whenever(meterTypeService.getById(1)).thenReturn(meterType)

        // When/Then
        mockMvc.perform(get("/api/energy/metertype/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Gas"))
    }

    @Test
    fun `should return not found when meter type does not exist`() {
        // Given
        whenever(meterTypeService.getById(999)).thenThrow(EntityNotFoundException("MeterType with id 999 not found"))

        // When/Then
        mockMvc.perform(get("/api/energy/metertype/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create new meter type`() {
        // Given
        val newMeterType = RestMeterType(id = 0, name = "Water")
        val createdEntity = MeterTypeEntity(id = 1, name = "Water")
        whenever(meterTypeService.create(any())).thenReturn(createdEntity)

        // When/Then
        mockMvc.perform(
            post("/api/energy/metertype")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMeterType))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Water"))
    }

    @Test
    fun `should update existing meter type`() {
        // Given
        val updateMeterType = RestMeterType(id = 1, name = "Updated Gas")
        val updatedEntity = MeterTypeEntity(id = 1, name = "Updated Gas")
        whenever(meterTypeService.save(any(), any())).thenReturn(updatedEntity)

        // When/Then
        mockMvc.perform(
            put("/api/energy/metertype/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMeterType))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Gas"))
    }
}