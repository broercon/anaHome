import jakarta.persistence.EntityNotFoundException
import org.broercon.anahome.energy.meter.MeterEntity
import org.broercon.anahome.energy.meter.MeterMapper
import org.broercon.anahome.energy.meter.MeterRepository
import org.broercon.anahome.energy.meter.MeterRest
import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.metertype.MeterTypeEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.broercon.anahome.Application
import org.broercon.anahome.energy.metertype.MeterTypeService
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import


@Import(TestContainersConfig::class)
@SpringBootTest(classes = [Application::class])
class MeterServiceTest {
    @Mock
    private lateinit var repository: MeterRepository

    @Mock
    private lateinit var meterTypeService: MeterTypeService

    @Mock
    private lateinit var mapper: MeterMapper

    @InjectMocks
    private lateinit var service: MeterService

    private lateinit var meterEntity: MeterEntity
    private lateinit var meterRest: MeterRest

    @BeforeEach
    fun setup() {
        meterEntity = MeterEntity(
            id = 1,
            name = "Test Meter",
            meterNumber = "12345",
            location = "Test Location",
            meterTypeEntity = MeterTypeEntity(1, "Test Type")
        )

        meterRest = MeterRest(
            id = 1,
            name = "Test Meter",
            meterNumber = "12345",
            location = "Test Location",
            meterTypeEntityId = 1
        )
    }

    @Test
    fun `getAll should return list of meters`() {
        // Given
        whenever(repository.findAll()).thenReturn(listOf(meterEntity))

        // When
        val result = service.getAll()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first()).isEqualTo(meterEntity)
        verify(repository).findAll()
    }

    @Test
    fun `getById should return meter when found`() {
        // Given
        whenever(repository.findById(1)).thenReturn(Optional.of(meterEntity))
        //whenever(mapper.toRest(meterEntity)).thenReturn(meterRest)

        // When
        val result = service.getById(1)

        // Then
        assertThat(result).isEqualTo(meterEntity)
        verify(repository).findById(1)
    }

    @Test
    fun `getById should throw exception when meter not found`() {
        // Given
        whenever(repository.findById(1)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<EntityNotFoundException> { service.getById(1) }
        verify(repository).findById(1)
    }

    @Test
    fun `create should save and return new meter`() {
        // Given
        whenever(repository.save(meterEntity)).thenReturn(meterEntity)

        // When
        val result: MeterEntity = service.create(meterEntity)

        // Then
        assertThat(result).isEqualTo(meterEntity)
        verify(repository).save(meterEntity)
    }

    @Test
    fun `update should modify existing meter`() {
        // Given
        whenever(repository.findById(1)).thenReturn(Optional.of(meterEntity))
        whenever(repository.save(meterEntity)).thenReturn(meterEntity)

        // When
        val result = service.save(1, meterEntity)

        // Then
        assertThat(result).isEqualTo(meterEntity)
        verify(repository).findById(1)
        verify(repository).save(meterEntity)
    }

    @Test
    fun `update should throw exception when meter not found`() {
        // Given
        whenever(repository.findById(1)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<EntityNotFoundException> { service.save(1, meterEntity) }
        verify(repository).findById(1)
        verify(repository, never()).save(any())
    }

    @Test
    fun `delete should remove existing meter`() {
        // Given
        whenever(repository.findById(1)).thenReturn(Optional.of(meterEntity))

        // When
        service.delete(1)

        // Then
        verify(repository).findById(1)
        verify(repository).deleteById(1)
    }

    @Test
    fun `delete should throw exception when meter not found`() {
        // Given
        whenever(repository.findById(1)).thenReturn(Optional.empty())

        // When/Then
        assertThrows<EntityNotFoundException> { service.delete(1) }
        verify(repository).findById(1)
        verify(repository, never()).deleteById(any())
    }
}