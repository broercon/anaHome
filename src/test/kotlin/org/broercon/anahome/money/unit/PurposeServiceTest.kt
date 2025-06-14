package org.broercon.anahome.money.unit

import org.broercon.anahome.money.purpose.PurposeEntity
import org.broercon.anahome.money.purpose.PurposeRepository
import org.broercon.anahome.money.purpose.PurposeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Import(TestContainersConfig::class)
@ExtendWith(MockitoExtension::class)
class PurposeServiceTest {

    @InjectMocks
    private lateinit var purposeService: PurposeService

    @Mock
    private lateinit var purposeRepository: PurposeRepository

    @BeforeEach
    fun setup() {
        // Clean up database
        purposeRepository.deleteAll()
    }

    @Test
    fun `should return all purpos`() {
        val purpos: List<PurposeEntity> = listOf(PurposeEntity(
            id = 1, name = "Vendor A",
            description = "Description Name Vendor A"
        ))
        whenever(purposeRepository.findAll()).thenReturn(purpos)

        val result = purposeService.findAll()

        assertEquals(1, result.size)
        assertEquals("Vendor A", result[0].name)
        verify(purposeRepository).findAll()
    }

    @Test
    fun `should return vendor by ID`() {
        val vendor = PurposeEntity(name = "Vendor B", description = "Description Name Vendor A")
        whenever(purposeRepository.findById(2L)).thenReturn(Optional.of(vendor))

        val result = purposeService.findById(2L)

        assertNotNull(result)
        assertEquals("Vendor B", result?.name)
        verify(purposeRepository).findById(2L)
    }

    @Test
    fun `should return null if vendor not found`() {
        whenever(purposeRepository.findById(999L)).thenReturn(Optional.empty())

        val result = purposeService.findById(999L)

        assertNull(result)
        verify(purposeRepository).findById(999L)
    }

    @Test
    fun `should save vendor`() {
        val vendor = PurposeEntity(id = 1, name = "New Vendor", description = "Description Name Vendor A" )
        whenever(purposeRepository.save(vendor)).thenReturn(vendor)

        val result = purposeService.save(vendor)

        assertEquals(1, result.id)
        verify(purposeRepository).save(vendor)
    }

    @Test
    fun `should delete vendor by ID`() {
        doNothing().`when`(purposeRepository).deleteById(1L)

        purposeService.deleteById(1L)

        verify(purposeRepository).deleteById(1L)
    }
}