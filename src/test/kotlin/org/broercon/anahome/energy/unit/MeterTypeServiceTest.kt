import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.Application
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.broercon.anahome.energy.metertype.MeterTypeService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


@Import(TestContainersConfig::class)
@SpringBootTest(classes = [Application::class])
class MeterTypeServiceTest {
    @Autowired
    private lateinit var meterTypeService: MeterTypeService

    @Test
    fun `should get meter type by id`() {
        // Given
        val meterType = MeterTypeEntity(id = 0, name = "Gas")
        val saved = meterTypeService.create(meterType)

        // When
        val result = meterTypeService.getById(saved.id)

        // Then
        assertNotNull(result)
        assertEquals("Gas", result.name)
    }

    @Test
    fun `should throw EntityNotFoundException when meter type not found`() {
        // When/Then
        assertThrows<EntityNotFoundException> {
            meterTypeService.getById(999)
        }
    }

    @Test
    fun `should delete Meter Type`() {
        // When/Then
        val meterType = MeterTypeEntity(id = 0, name = "Gas")
        val saved = meterTypeService.create(meterType)

        // When
        var result = meterTypeService.getById(saved.id)

        // Then
        assertNotNull(result)

        meterTypeService.delete(saved.id)

        assertThrows<EntityNotFoundException> {
            meterTypeService.getById(saved.id)
        }
    }

    @Test
    fun `should throw exception when delete with Meter Type not exists`() {
        // When/Then
        assertThrows<EntityNotFoundException> {
            meterTypeService.delete(999)
        }
    }

    @AfterEach
    fun cleanup() {
        // Add cleanup code here if needed
    }
}